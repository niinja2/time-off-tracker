package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
class ApprovalApiTest {

    private static final Instant NOW = Instant.parse("2026-10-30T10:00:00Z");

    private static final String FORBIDDEN = "You cannot approve or reject this request";
    private static final String NOT_FOUND = "Request not found";
    private static final String BLANK_COMMENT = "comment must not be blank";
    private static final String COMMENT = "{\"comment\":\"Busy week\"}";

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
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    // R1: voditelj odlučuje o zahtjevima članova svog tima

    @Test
    void anaApprovesIvansRequest_R1() throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "approve",
            "{\"comment\":\"ok\"}");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        Number decidedById = JsonPath.read(body, "$.decidedById");
        assertThat(JsonPath.<String>read(body, "$.status")).isEqualTo("APPROVED");
        assertThat(JsonPath.<String>read(body, "$.ownerName")).isEqualTo("Ivan");
        assertThat(JsonPath.<String>read(body, "$.decisionComment")).isEqualTo("ok");
        assertThat(decidedById.longValue()).isEqualTo(userId("ana"));
        assertThat(JsonPath.<String>read(body, "$.decidedAt")).isEqualTo("2026-10-30T10:00:00Z");
        assertThat(statusOf(id)).isEqualTo("APPROVED");
        assertThat(deciderOf(id)).isEqualTo(userId("ana"));
    }

    @Test
    void anaRejectsIvansRequest_R1() throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "reject",
            COMMENT);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        Number decidedById = JsonPath.read(body, "$.decidedById");
        assertThat(JsonPath.<String>read(body, "$.status")).isEqualTo("REJECTED");
        assertThat(JsonPath.<String>read(body, "$.decisionComment")).isEqualTo("Busy week");
        assertThat(decidedById.longValue()).isEqualTo(userId("ana"));
        assertThat(JsonPath.<String>read(body, "$.decidedAt")).isEqualTo("2026-10-30T10:00:00Z");
        assertThat(statusOf(id)).isEqualTo("REJECTED");
    }

    @Test
    void lukaApprovesSarasRequest_R1() throws Exception {
        long id = pending("sara");

        HttpResponse<String> response = decide(
            "luka",
            id,
            "approve",
            "");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"approve", "reject"})
    void lukaCannotDecideIvansRequest_R1(String action) throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = decide(
            "luka",
            id,
            action,
            COMMENT);

        assertProblem(response, 403, FORBIDDEN);
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void ivanCannotApproveMarkosRequest_R1() throws Exception {
        long id = pending("marko");

        HttpResponse<String> response = decide(
            "ivan",
            id,
            "approve",
            "");

        assertProblem(response, 403, FORBIDDEN);
    }

    // Q5.1: član više timova, dovoljan je voditelj bilo kojeg

    @Test
    void lukaApprovesIvanWhenIvanIsAlsoInMobile_Q5_1() throws Exception {
        addMember("Mobile", "ivan");
        long id = pending("ivan");

        HttpResponse<String> byLuka = decide(
            "luka",
            id,
            "approve",
            "");

        assertThat(byLuka.statusCode()).isEqualTo(200);
    }

    // R2: admin odlučuje o bilo kojem zahtjevu

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ivan", "sara", "tom"})
    void evaApprovesAnyEmployeesRequest_R2(String owner) throws Exception {
        long id = pending(owner);

        HttpResponse<String> response = decide(
            "eva",
            id,
            "approve",
            "");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void evaRejectsTomsRequest_R2() throws Exception {
        long id = pending("tom");

        HttpResponse<String> response = decide(
            "eva",
            id,
            "reject",
            COMMENT);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("REJECTED");
    }

    // R3: nitko ne odlučuje o svom zahtjevu, osim admina

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
        "ivan, approve",
        "ivan, reject",
        "ana, approve",
        "ana, reject"
    })
    void ownerCannotDecideOwnRequest_R3(String owner, String action) throws Exception {
        long id = pending(owner);

        HttpResponse<String> response = decide(
            owner,
            id,
            action,
            COMMENT);

        assertProblem(response, 403, FORBIDDEN);
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    // R3, D2: zahtjev voditelja ide adminu

    @Test
    void lukaCannotApproveAnasRequest_D2() throws Exception {
        long id = pending("ana");

        HttpResponse<String> response = decide(
            "luka",
            id,
            "approve",
            "");

        assertProblem(response, 403, FORBIDDEN);
    }

    @Test
    void lukaCannotApproveAnasRequestEvenWhenAnaIsInMobile_D2() throws Exception {
        addMember("Mobile", "ana");
        long id = pending("ana");

        HttpResponse<String> response = decide(
            "luka",
            id,
            "approve",
            "");

        assertProblem(response, 403, FORBIDDEN);
    }

    @Test
    void evaApprovesAnasRequest_D2() throws Exception {
        long id = pending("ana");

        HttpResponse<String> response = decide(
            "eva",
            id,
            "approve",
            "");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // Q1.1, D15: admin smije odobriti svoj zahtjev, bilo kojeg tipa

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"VACATION", "SICK_LEAVE", "DAY_OFF"})
    void evaApprovesOwnRequestOfAnyType_D15(String type) throws Exception {
        long id = insertRequest(
            "eva",
            type,
            "2026-11-02",
            "2026-11-06",
            "PENDING");

        HttpResponse<String> response = decide(
            "eva",
            id,
            "approve",
            "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("APPROVED");
    }

    // Q4: zahtjev korisnika bez tima odobrava samo admin

    @Test
    void anaCannotApproveEvasRequest_Q4() throws Exception {
        long id = pending("eva");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "approve",
            "");

        assertProblem(response, 403, FORBIDDEN);
    }

    @Test
    void onlyAdminDecidesRequestOfUserWithoutTeam_Q4() throws Exception {
        jdbc.update("INSERT INTO users (username, name, password_hash, hire_date)"
            + " VALUES ('nina', 'Nina', 'x', '2020-01-01')");
        long id = pending("nina");

        HttpResponse<String> byAna = decide(
            "ana",
            id,
            "approve",
            "");
        HttpResponse<String> byLuka = decide(
            "luka",
            id,
            "approve",
            "");
        HttpResponse<String> byEva = decide(
            "eva",
            id,
            "approve",
            "");

        assertProblem(byAna, 403, FORBIDDEN);
        assertProblem(byLuka, 403, FORBIDDEN);
        assertThat(byEva.statusCode()).isEqualTo(200);
    }

    // R4: odlučuje se samo o zahtjevu na čekanju

    @ParameterizedTest(name = "{1} on {0}")
    @CsvSource({
        "APPROVED, approve, Request cannot change from APPROVED to APPROVED",
        "REJECTED, approve, Request cannot change from REJECTED to APPROVED",
        "CANCELLED, approve, Request cannot change from CANCELLED to APPROVED",
        "APPROVED, reject, Request cannot change from APPROVED to REJECTED",
        "REJECTED, reject, Request cannot change from REJECTED to REJECTED",
        "CANCELLED, reject, Request cannot change from CANCELLED to REJECTED"
    })
    void decidedRequestCannotBeDecidedAgain_R4(String status, String action, String detail) throws Exception {
        long id = insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            status);

        HttpResponse<String> response = decide(
            "ana",
            id,
            action,
            COMMENT);

        assertProblem(response, 409, detail);
        assertThat(statusOf(id)).isEqualTo(status);
    }

    // R5: odbijanje traži komentar, odobrenje ne

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"", "{}", "{\"comment\":null}", "{\"comment\":\"\"}", "{\"comment\":\"   \"}"})
    void rejectionWithoutCommentIsBadRequest_R5(String json) throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "reject",
            json);

        assertProblem(response, 400, BLANK_COMMENT);
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void approvalWithoutBodyStoresNoComment_R5() throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "approve",
            "");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.decisionComment")).isNull();
    }

    @Test
    void approvalWithBlankCommentStoresNoComment_T5() throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "approve",
            "{\"comment\":\"   \"}");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.decisionComment")).isNull();
    }

    // Provjera 1: duljina komentara

    @Test
    void commentOf500CharactersIsAccepted_T5() throws Exception {
        long id = pending("ivan");
        String json = "{\"comment\":\"" + "x".repeat(500) + "\"}";

        HttpResponse<String> response = decide(
            "ana",
            id,
            "reject",
            json);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void commentOf501CharactersIsBadRequest_T5() throws Exception {
        long id = pending("ivan");
        String json = "{\"comment\":\"" + "x".repeat(501) + "\"}";

        HttpResponse<String> response = decide(
            "ana",
            id,
            "reject",
            json);

        assertBadField(response, "comment");
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void nonNumericIdIsBadRequest_T5() throws Exception {
        String token = api.tokenOf("ana");

        HttpResponse<String> response = api.post("/api/requests/abc/approve", token, "");

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // Q35, Q36: zahtjev na čekanju iz prošlosti smije se odobriti

    @Test
    void pastPendingRequestCanBeApproved_Q35() throws Exception {
        long id = insertRequest(
            "ivan",
            "VACATION",
            "2026-01-05",
            "2026-01-09",
            "PENDING");

        HttpResponse<String> response = decide(
            "ana",
            id,
            "approve",
            "");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // R9, R13: kod odobrenja se preklapanje i saldo ne provjeravaju ponovo

    @Test
    void approvalDoesNotRecheckOverlap_R9() throws Exception {
        long first = pending("ivan");
        long second = insertRequest(
            "ivan",
            "VACATION",
            "2026-11-04",
            "2026-11-10",
            "PENDING");

        HttpResponse<String> firstResponse = decide(
            "ana",
            first,
            "approve",
            "");
        HttpResponse<String> secondResponse = decide(
            "ana",
            second,
            "approve",
            "");

        assertThat(firstResponse.statusCode()).isEqualTo(200);
        assertThat(secondResponse.statusCode()).isEqualTo(200);
    }

    @Test
    void approvalDoesNotRecheckBalance_R13() throws Exception {
        long id = insertRequest(
            "tom",
            "VACATION",
            "2026-06-01",
            "2026-06-17",
            "PENDING");

        HttpResponse<String> response = decide(
            "luka",
            id,
            "approve",
            "");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // Nepostojeći zahtjev, bez prijave

    @Test
    void unknownRequestIsNotFound_T5() throws Exception {
        HttpResponse<String> response = decide(
            "ana",
            999999,
            "approve",
            "");

        assertProblem(response, 404, NOT_FOUND);
    }

    @Test
    void approveWithoutTokenIsUnauthorized_T5() throws Exception {
        long id = pending("ivan");

        HttpResponse<String> response = api.post("/api/requests/" + id + "/approve", null, "");

        assertProblem(response, 401, "Authentication required");
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    // Parovi: redoslijed provjera, prva koja padne određuje odgovor

    @Test
    void missingCommentIsReportedBeforeUnknownRequest_T5() throws Exception {
        HttpResponse<String> response = decide(
            "luka",
            999999,
            "reject",
            "{}");

        assertProblem(response, 400, BLANK_COMMENT);
    }

    @Test
    void unknownRequestIsReportedBeforePermission_T5() throws Exception {
        HttpResponse<String> response = decide(
            "ivan",
            999999,
            "approve",
            "");

        assertProblem(response, 404, NOT_FOUND);
    }

    @Test
    void permissionIsCheckedBeforeStatus_T5() throws Exception {
        long id = insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = decide(
            "luka",
            id,
            "approve",
            "");

        assertProblem(response, 403, FORBIDDEN);
    }

    // GET /api/approvals

    @Test
    void anaSeesPendingRequestsOfHerTeamSortedByStartDate_R1() throws Exception {
        pending("ivan", "2026-11-16", "2026-11-16");
        pending("marko", "2026-11-02", "2026-11-02");
        pending("petra", "2026-11-02", "2026-11-02");
        pending("ana", "2026-11-09", "2026-11-09");
        pending("sara", "2026-11-09", "2026-11-09");
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-23",
            "2026-11-23",
            "APPROVED");

        assertThat(approvalOwners("ana")).containsExactly("Marko", "Petra", "Ivan");
    }

    @Test
    void evaSeesAllPendingRequestsIncludingOwn_R2() throws Exception {
        pending("ivan", "2026-11-02", "2026-11-02");
        pending("sara", "2026-11-03", "2026-11-03");
        pending("ana", "2026-11-04", "2026-11-04");
        pending("eva", "2026-11-05", "2026-11-05");
        insertRequest(
            "tom",
            "VACATION",
            "2026-11-06",
            "2026-11-06",
            "REJECTED");

        assertThat(approvalOwners("eva")).containsExactly("Ivan", "Sara", "Ana", "Eva");
    }

    @Test
    void lukaSeesHisTeamButNotAnotherLead_D2() throws Exception {
        addMember("Mobile", "ana");
        pending("sara", "2026-11-02", "2026-11-02");
        pending("tom", "2026-11-03", "2026-11-03");
        pending("ana", "2026-11-04", "2026-11-04");
        pending("ivan", "2026-11-05", "2026-11-05");

        assertThat(approvalOwners("luka")).containsExactly("Sara", "Tom");
    }

    @Test
    void employeeGetsEmptyApprovalList_T5() throws Exception {
        pending("marko", "2026-11-02", "2026-11-02");
        String token = api.tokenOf("ivan");

        HttpResponse<String> response = api.get("/api/approvals", token);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        List<Object> requests = JsonPath.read(body, "$");
        assertThat(requests).isEmpty();
    }

    @Test
    void approvalsWithoutTokenIsUnauthorized_T5() throws Exception {
        HttpResponse<String> response = api.get("/api/approvals", null);

        assertProblem(response, 401, "Authentication required");
    }

    // Pomoćne metode

    private HttpResponse<String> decide(
        String actor,
        long requestId,
        String action,
        String json) throws Exception {
        String token = api.tokenOf(actor);
        String path = "/api/requests/" + requestId + "/" + action;
        return api.post(path, token, json);
    }

    private List<String> approvalOwners(String actor) throws Exception {
        String token = api.tokenOf(actor);
        HttpResponse<String> response = api.get("/api/approvals", token);
        String body = response.body();
        assertThat(response.statusCode()).isEqualTo(200);
        return JsonPath.read(body, "$[*].ownerName");
    }

    private long pending(String owner) {
        return pending(owner, "2026-11-02", "2026-11-06");
    }

    private long pending(String owner, String start, String end) {
        return insertRequest(
            owner,
            "VACATION",
            start,
            end,
            "PENDING");
    }

    private long insertRequest(
        String username,
        String type,
        String start,
        String end,
        String status) {
        return jdbc.queryForObject(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " SELECT id, ?, ?, ?, ?, now() FROM users WHERE username = ? RETURNING id",
            Long.class,
            type,
            LocalDate.parse(start),
            LocalDate.parse(end),
            status,
            username);
    }

    private void addMember(String team, String username) {
        jdbc.update(
            "INSERT INTO team_members (team_id, user_id)"
                + " SELECT t.id, u.id FROM teams t, users u WHERE t.name = ? AND u.username = ?",
            team,
            username);
    }

    private void cleanUp() {
        jdbc.update("DELETE FROM time_off_requests");
        jdbc.update("DELETE FROM team_members"
            + " WHERE team_id = (SELECT id FROM teams WHERE name = 'Mobile')"
            + " AND user_id IN (SELECT id FROM users WHERE username IN ('ana', 'ivan'))");
        jdbc.update("DELETE FROM users WHERE username = 'nina'");
    }

    private String statusOf(long id) {
        return jdbc.queryForObject("SELECT status FROM time_off_requests WHERE id = ?", String.class, id);
    }

    private long deciderOf(long id) {
        return jdbc.queryForObject("SELECT decided_by_id FROM time_off_requests WHERE id = ?", Long.class, id);
    }

    private long userId(String username) {
        return jdbc.queryForObject("SELECT id FROM users WHERE username = ?", Long.class, username);
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
    }
}
