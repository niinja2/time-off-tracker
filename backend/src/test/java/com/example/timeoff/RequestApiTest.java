package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class RequestApiTest {

    private static final Instant NOW = Instant.parse("2026-10-30T09:00:00Z");

    private static final String OVERLAP = "Request overlaps an existing pending or approved request";
    private static final String BEFORE_HIRE = "Request must not start before hire date";
    private static final String END_BEFORE_START = "End date must not be before start date";
    private static final String ONE_YEAR = "Request must be within one calendar year";
    private static final String NO_WORKING_DAY = "Request must contain at least one working day";
    private static final String REASON_TOO_LONG = "reason size must be between 0 and 500";

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClockConfiguration {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW, ZoneId.of("Europe/Zagreb"));
        }
    }

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    private ApiClient api;

    @BeforeEach
    void setUp() {
        api = new ApiClient(port);
        jdbc.update("DELETE FROM time_off_requests");
    }

    @AfterEach
    void tearDown() {
        jdbc.update("DELETE FROM time_off_requests");
    }

    // Osnovni: ispravan zahtjev

    @Test
    void vacationRequestIsCreatedAsPending_T4() throws Exception {
        String json = "{\"type\":\"VACATION\",\"startDate\":\"2026-11-02\","
            + "\"endDate\":\"2026-11-06\",\"reason\":\"Trip\"}";
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), json);
        String body = response.body();
        Number ownerId = JsonPath.read(body, "$.ownerId");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(JsonPath.<Integer>read(body, "$.id")).isNotNull();
        assertThat(ownerId.longValue()).isEqualTo(userId("ivan"));
        assertThat(JsonPath.<String>read(body, "$.type")).isEqualTo("VACATION");
        assertThat(JsonPath.<String>read(body, "$.startDate")).isEqualTo("2026-11-02");
        assertThat(JsonPath.<String>read(body, "$.endDate")).isEqualTo("2026-11-06");
        assertThat(JsonPath.<String>read(body, "$.reason")).isEqualTo("Trip");
        assertThat(JsonPath.<String>read(body, "$.status")).isEqualTo("PENDING");
        assertThat(JsonPath.<String>read(body, "$.createdAt")).isEqualTo("2026-10-30T09:00:00Z");
    }

    @Test
    void createdRequestIsStoredInDatabase_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-02", "2026-11-06");
        Number id = JsonPath.read(response.body(), "$.id");

        String status = jdbc.queryForObject(
            "SELECT status FROM time_off_requests WHERE id = ?",
            String.class,
            id.longValue());
        Long ownerId = jdbc.queryForObject(
            "SELECT owner_id FROM time_off_requests WHERE id = ?",
            Long.class,
            id.longValue());
        Timestamp createdAt = jdbc.queryForObject(
            "SELECT created_at FROM time_off_requests WHERE id = ?",
            Timestamp.class,
            id.longValue());

        assertThat(status).isEqualTo("PENDING");
        assertThat(ownerId).isEqualTo(userId("ivan"));
        assertThat(createdAt.toInstant()).isEqualTo(NOW);
    }

    @Test
    void sickLeaveRequestIsCreated_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "SICK_LEAVE", "2026-11-02", "2026-11-03");
        String body = response.body();
        String type = JsonPath.read(body, "$.type");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(type).isEqualTo("SICK_LEAVE");
    }

    @Test
    void dayOffRequestIsCreated_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "DAY_OFF", "2026-11-02", "2026-11-02");
        String body = response.body();
        String type = JsonPath.read(body, "$.type");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(type).isEqualTo("DAY_OFF");
    }

    @Test
    void reasonMayBeOmitted_brief() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-02", "2026-11-02");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(storedReasons("ivan")).containsExactly((String) null);
    }

    @Test
    void blankReasonIsStoredAsNull_T4() throws Exception {
        String json = "{\"type\":\"VACATION\",\"startDate\":\"2026-11-02\","
            + "\"endDate\":\"2026-11-02\",\"reason\":\"   \"}";
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), json);

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(storedReasons("ivan")).containsExactly((String) null);
    }

    @Test
    void withoutTokenIsUnauthorized_T4() throws Exception {
        String json = body("VACATION", "2026-11-02", "2026-11-06");
        HttpResponse<String> response = api.post("/api/requests", null, json);

        assertProblem(response, 401, "Authentication required");
        assertThat(requestCount()).isZero();
    }

    // Q29: vlasnik je uvijek prijavljeni korisnik

    @Test
    void teamLeadOwnsOwnRequest_Q29() throws Exception {
        HttpResponse<String> response = create("ana", "VACATION", "2026-11-02", "2026-11-02");
        Number ownerId = JsonPath.read(response.body(), "$.ownerId");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(ownerId.longValue()).isEqualTo(userId("ana"));
    }

    @Test
    void adminOwnsOwnRequest_Q29() throws Exception {
        HttpResponse<String> response = create("eva", "VACATION", "2026-11-02", "2026-11-02");
        Number ownerId = JsonPath.read(response.body(), "$.ownerId");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(ownerId.longValue()).isEqualTo(userId("eva"));
    }

    @Test
    void ownerIdInBodyDoesNotCreateRequestForSomeoneElse_Q29() throws Exception {
        String json = "{\"ownerId\":" + userId("ivan") + ",\"type\":\"VACATION\","
            + "\"startDate\":\"2026-11-02\",\"endDate\":\"2026-11-02\"}";
        api.post("/api/requests", api.tokenOf("eva"), json);

        assertThat(storedReasons("ivan")).isEmpty();
    }

    // Provjera 1: oblik ulaza

    @Test
    void missingTypeIsBadRequest_T4() throws Exception {
        String json = "{\"startDate\":\"2026-11-02\",\"endDate\":\"2026-11-06\"}";
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), json);

        assertBadField(response, "type");
    }

    @Test
    void missingStartDateIsBadRequest_T4() throws Exception {
        String json = "{\"type\":\"VACATION\",\"endDate\":\"2026-11-06\"}";
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), json);

        assertBadField(response, "startDate");
    }

    @Test
    void missingEndDateIsBadRequest_T4() throws Exception {
        String json = "{\"type\":\"VACATION\",\"startDate\":\"2026-11-02\"}";
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), json);

        assertBadField(response, "endDate");
    }

    @Test
    void unknownTypeIsBadRequest_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "HOLIDAY", "2026-11-02", "2026-11-06");

        assertBadField(response, "type");
    }

    @Test
    void invalidDateIsBadRequest_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-13-01", "2026-11-06");

        assertBadField(response, "startDate");
    }

    @Test
    void bodyThatIsNotJsonIsBadRequest_T4() throws Exception {
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), "not json");
        String contentType = response.headers()
            .firstValue("Content-Type")
            .orElse("");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(contentType).startsWith("application/problem+json");
        assertThat(requestCount()).isZero();
    }

    // Provjera 2: duljina razloga

    @Test
    void reasonOf500CharactersIsAccepted_T4() throws Exception {
        HttpResponse<String> response = createWithReason("ivan", "x".repeat(500));

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void reasonOf501CharactersIsBadRequest_T4() throws Exception {
        HttpResponse<String> response = createWithReason("ivan", "x".repeat(501));

        assertProblem(response, 400, REASON_TOO_LONG);
        assertThat(requestCount()).isZero();
    }

    // Provjera 3: R8

    @Test
    void endDateBeforeStartDateIsBadRequest_R8() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-06", "2026-11-02");

        assertProblem(response, 400, END_BEFORE_START);
        assertThat(requestCount()).isZero();
    }

    @Test
    void endDateOneDayBeforeStartDateIsBadRequest_R8() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-03", "2026-11-02");

        assertProblem(response, 400, END_BEFORE_START);
    }

    @Test
    void endDateEqualToStartDateOnWorkingDayIsAccepted_R8() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-02", "2026-11-02");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    // Provjera 4: jedna kalendarska godina

    @Test
    void requestAcrossYearBoundaryIsBadRequest_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-12-31", "2027-01-04");

        assertProblem(response, 400, ONE_YEAR);
        assertThat(requestCount()).isZero();
    }

    @Test
    void requestEndingOnLastDayOfYearIsAccepted_T4() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-12-28", "2026-12-31");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    // Provjera 5: R10

    @Test
    void weekendOnlyRequestIsBadRequest_R10() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-07", "2026-11-08");

        assertProblem(response, 400, NO_WORKING_DAY);
        assertThat(requestCount()).isZero();
    }

    @Test
    void weekendOnlySickLeaveIsBadRequest_R10() throws Exception {
        HttpResponse<String> response = create("ivan", "SICK_LEAVE", "2026-11-07", "2026-11-08");

        assertProblem(response, 400, NO_WORKING_DAY);
    }

    // Provjera 6: D4

    @Test
    void tomCannotStartBeforeHireDate_D4() throws Exception {
        HttpResponse<String> response = create("tom", "VACATION", "2026-05-29", "2026-06-02");

        assertProblem(response, 409, BEFORE_HIRE);
        assertThat(requestCount()).isZero();
    }

    @Test
    void tomCanStartOnHireDate_D4() throws Exception {
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-01", "2026-06-02");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void tomCannotTakeSickLeaveBeforeHireDate_D4() throws Exception {
        HttpResponse<String> response = create("tom", "SICK_LEAVE", "2026-05-29", "2026-05-29");

        assertProblem(response, 409, BEFORE_HIRE);
    }

    // Provjera 7: R9

    @Test
    void overlapWithPendingIsConflict_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "PENDING");
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-04", "2026-11-10");

        assertProblem(response, 409, OVERLAP);
        assertThat(requestCount()).isEqualTo(1);
    }

    @Test
    void overlapWithApprovedIsConflict_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "APPROVED");
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-04", "2026-11-10");

        assertProblem(response, 409, OVERLAP);
    }

    @Test
    void overlapWithOtherTypeIsConflict_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "APPROVED");
        HttpResponse<String> response = create("ivan", "SICK_LEAVE", "2026-11-04", "2026-11-04");

        assertProblem(response, 409, OVERLAP);
    }

    @Test
    void touchingOnEdgeIsConflict_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "PENDING");
        HttpResponse<String> response = create("ivan", "DAY_OFF", "2026-11-06", "2026-11-10");

        assertProblem(response, 409, OVERLAP);
    }

    @Test
    void touchingOnStartEdgeIsConflict_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-09",
            "2026-11-13",
            "PENDING");
        HttpResponse<String> response = create("ivan", "DAY_OFF", "2026-11-05", "2026-11-09");

        assertProblem(response, 409, OVERLAP);
    }

    @Test
    void requestStartingDayAfterExistingEndsIsAccepted_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "PENDING");
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-07", "2026-11-10");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void overlapWithRejectedIsAccepted_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "REJECTED");
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-02", "2026-11-06");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void overlapWithCancelledIsAccepted_R9() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "CANCELLED");
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-02", "2026-11-06");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void overlapWithSomeoneElsesRequestIsAccepted_R9() throws Exception {
        insertRequest(
            "marko",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "APPROVED");
        HttpResponse<String> response = create("ivan", "VACATION", "2026-11-02", "2026-11-06");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    // Provjera 8: R13, R14

    @Test
    void tomCanUseExactlyAllTwelveDays_R13() throws Exception {
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-01", "2026-06-16");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void tomCannotRequestThirteenDays_R13() throws Exception {
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-01", "2026-06-17");

        assertProblem(response, 409, "Not enough vacation days: requested 13, remaining 12");
        assertThat(requestCount()).isZero();
    }

    @Test
    void pendingVacationUsesBalance_R13() throws Exception {
        insertRequest(
            "tom",
            "VACATION",
            "2026-06-01",
            "2026-06-05",
            "PENDING");
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-08", "2026-06-17");

        assertProblem(response, 409, "Not enough vacation days: requested 8, remaining 7");
    }

    @Test
    void pendingVacationLeavesExactRemainderUsable_R13() throws Exception {
        insertRequest(
            "tom",
            "VACATION",
            "2026-06-01",
            "2026-06-05",
            "PENDING");
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-08", "2026-06-16");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void approvedVacationUsesBalance_R13() throws Exception {
        insertRequest(
            "tom",
            "VACATION",
            "2026-06-01",
            "2026-06-05",
            "APPROVED");
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-08", "2026-06-17");

        assertProblem(response, 409, "Not enough vacation days: requested 8, remaining 7");
    }

    @Test
    void sickLeaveOverBalanceIsAccepted_R14() throws Exception {
        HttpResponse<String> response = create("tom", "SICK_LEAVE", "2026-06-01", "2026-06-17");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void dayOffOverBalanceIsAccepted_R14() throws Exception {
        HttpResponse<String> response = create("tom", "DAY_OFF", "2026-06-01", "2026-06-17");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void vacationNextYearUsesNextYearsBalance_D5() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-01-05",
            "2026-01-30",
            "APPROVED");
        HttpResponse<String> response = create("ivan", "VACATION", "2027-01-04", "2027-01-08");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void nextYearsUsedBalanceBlocksNextYearOnly_D5() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2027-01-04",
            "2027-01-29",
            "APPROVED");
        HttpResponse<String> nextYear = create("ivan", "VACATION", "2027-02-01", "2027-02-01");
        HttpResponse<String> thisYear = create("ivan", "VACATION", "2026-11-02", "2026-11-02");

        assertProblem(nextYear, 409, "Not enough vacation days: requested 1, remaining 0");
        assertThat(thisYear.statusCode()).isEqualTo(201);
    }

    // Q19: datumi u prošlosti

    @Test
    void pastDateIsAccepted_Q19() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2026-01-05", "2026-01-05");

        assertThat(response.statusCode()).isEqualTo(201);
    }

    // Parovi: redoslijed provjera, prva koja padne određuje odgovor

    @Test
    void tooLongReasonIsReportedBeforeDateOrder_T4() throws Exception {
        String json = "{\"type\":\"VACATION\",\"startDate\":\"2026-11-06\","
            + "\"endDate\":\"2026-11-02\",\"reason\":\"" + "x".repeat(501) + "\"}";
        HttpResponse<String> response = api.post("/api/requests", api.tokenOf("ivan"), json);

        assertProblem(response, 400, REASON_TOO_LONG);
    }

    @Test
    void dateOrderIsReportedBeforeYearBoundary_R8() throws Exception {
        HttpResponse<String> response = create("ivan", "VACATION", "2027-01-04", "2026-12-31");

        assertProblem(response, 400, END_BEFORE_START);
    }

    @Test
    void noWorkingDayIsReportedBeforeHireDate_R10() throws Exception {
        HttpResponse<String> response = create("tom", "VACATION", "2026-05-30", "2026-05-31");

        assertProblem(response, 400, NO_WORKING_DAY);
    }

    @Test
    void hireDateIsReportedBeforeBalance_D4() throws Exception {
        HttpResponse<String> response = create("tom", "VACATION", "2026-05-29", "2026-06-17");

        assertProblem(response, 409, BEFORE_HIRE);
    }

    @Test
    void overlapIsReportedBeforeBalance_R9() throws Exception {
        insertRequest(
            "tom",
            "VACATION",
            "2026-06-01",
            "2026-06-05",
            "PENDING");
        HttpResponse<String> response = create("tom", "VACATION", "2026-06-05", "2026-06-30");

        assertProblem(response, 409, OVERLAP);
    }

    // Pomoćne metode

    private HttpResponse<String> create(
        String username,
        String type,
        String start,
        String end) throws Exception {
        String token = api.tokenOf(username);
        return api.post("/api/requests", token, body(type, start, end));
    }

    private HttpResponse<String> createWithReason(String username, String reason) throws Exception {
        String json = "{\"type\":\"VACATION\",\"startDate\":\"2026-11-02\","
            + "\"endDate\":\"2026-11-02\",\"reason\":\"" + reason + "\"}";
        String token = api.tokenOf(username);
        return api.post("/api/requests", token, json);
    }

    private static String body(String type, String start, String end) {
        return "{\"type\":\"" + type + "\",\"startDate\":\"" + start
            + "\",\"endDate\":\"" + end + "\"}";
    }

    private void insertRequest(
        String username,
        String type,
        String start,
        String end,
        String status) {
        jdbc.update(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " SELECT id, ?, ?, ?, ?, now() FROM users WHERE username = ?",
            type,
            LocalDate.parse(start),
            LocalDate.parse(end),
            status,
            username);
    }

    private long userId(String username) {
        return jdbc.queryForObject("SELECT id FROM users WHERE username = ?", Long.class, username);
    }

    private int requestCount() {
        return jdbc.queryForObject("SELECT count(*) FROM time_off_requests", Integer.class);
    }

    private List<String> storedReasons(String username) {
        return jdbc.queryForList(
            "SELECT reason FROM time_off_requests"
                + " WHERE owner_id = (SELECT id FROM users WHERE username = ?)",
            String.class,
            username);
    }

    private void assertProblem(HttpResponse<String> response, int status, String detail) {
        String contentType = response.headers()
            .firstValue("Content-Type")
            .orElse("");
        String body = response.body();
        String actualDetail = JsonPath.read(body, "$.detail");

        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(contentType).startsWith("application/problem+json");
        assertThat(actualDetail).isEqualTo(detail);
    }

    private void assertBadField(HttpResponse<String> response, String field) {
        String contentType = response.headers()
            .firstValue("Content-Type")
            .orElse("");
        String body = response.body();
        String detail = JsonPath.read(body, "$.detail");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(contentType).startsWith("application/problem+json");
        assertThat(detail).contains(field);
        assertThat(requestCount()).isZero();
    }
}
