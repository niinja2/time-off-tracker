package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

/**
 * T6 Otkazivanje zahtjeva: POST /api/requests/{id}/cancel.
 *
 * Izvori: specifikacija 4; sučelje T6 (Ovlasti, Provjere) i T5 (Ovlasti); L25, L26.
 * Mreža: uloga (vlasnik: zaposlenik, voditelj, admin; tko smije odobriti: voditelj tima,
 *   voditelj preko drugog tima, admin; tko ne smije: kolega, voditelj drugog tima,
 *   voditelj na zahtjev voditelja, voditelj na zahtjev bez tima, zaposlenik na zahtjev voditelja)
 *   × stanje (PENDING, APPROVED, REJECTED, CANCELLED)
 *   × početak (prije danas, danas, sutra; na čekanju i s prošlim datumima)
 *   × razlog (bez tijela, {}, null, prazan, razmaci, 1, 500 i 501 znak);
 *   id (nije broj, negativan, ne postoji); bez prijave;
 *   redoslijed provjera: 1 prije 2, 2 prije 3, 3 prije 4, 5 i 6, 4 prije 5 i 6.
 * Pokriva: R4, R6, R7, Q4, Q23, Q30, Q30.1, Q31, Q31.1, Q32, D2, D3, D14, T6
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class CancelApiTest {

    // Petak 30. 10. 2026. u Zagrebu: "danas" je 2026-10-30, "sutra" 2026-10-31
    private static final Instant NOW = Instant.parse("2026-10-30T10:00:00Z");

    private static final String FORBIDDEN = "You cannot cancel this request";
    private static final String NOT_FOUND = "Request not found";
    private static final String BLANK_REASON = "reason must not be blank";
    private static final String STARTED = "Request has already started";
    private static final String REASON = "{\"reason\":\"Plans changed\"}";

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

    // R6: vlasnik otkazuje svoj zahtjev na čekanju, razlog opcionalan

    @Test
    void ivanCancelsOwnPendingRequestWithoutBody_R6() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("ivan", id, "");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        Number cancelledById = JsonPath.read(body, "$.cancelledById");
        Object cancelReason = JsonPath.read(body, "$.cancelReason");
        assertThat(JsonPath.<String>read(body, "$.status")).isEqualTo("CANCELLED");
        assertThat(cancelledById.longValue()).isEqualTo(userId("ivan"));
        assertThat(JsonPath.<String>read(body, "$.cancelledAt")).isEqualTo("2026-10-30T10:00:00Z");
        assertThat(cancelReason).isNull();
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
        assertThat(cancellerOf(id)).isEqualTo(userId("ivan"));
    }

    @Test
    void ownerReasonIsStored_R6() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("ivan", id, REASON);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.cancelReason")).isEqualTo("Plans changed");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"{}", "{\"reason\":\"\"}", "{\"reason\":\"   \"}"})
    void ownerBlankReasonIsStoredAsNull_R6(String json) throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("ivan", id, json);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        Object cancelReason = JsonPath.read(body, "$.cancelReason");
        assertThat(cancelReason).isNull();
    }

    @Test
    void evaCancelsOwnRequestWithoutReason_R6() throws Exception {
        long id = request("eva", "PENDING");

        HttpResponse<String> response = cancel("eva", id, "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
    }

    @Test
    void anaCancelsOwnRequestWithoutReason_R6() throws Exception {
        long id = request("ana", "PENDING");

        HttpResponse<String> response = cancel("ana", id, "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
    }

    @Test
    void ownerCancelsPastPendingRequest_R6() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-01-05",
            "2026-01-09",
            "PENDING");

        HttpResponse<String> response = cancel("ivan", id, "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
    }

    // R7, Q23: odobreni zahtjev vlasnik otkazuje dok nije počeo; koji počinje danas je počeo

    @Test
    void ivanCancelsOwnApprovedRequestStartingTomorrow_R7() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-10-31",
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = cancel("ivan", id, "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"2026-10-30", "2026-10-26"})
    void ownerCannotCancelStartedApprovedRequest_R7(String start) throws Exception {
        long id = insertRequest(
            "ivan",
            start,
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = cancel("ivan", id, "");

        assertProblem(response, 409, STARTED);
        assertThat(statusOf(id)).isEqualTo("APPROVED");
    }

    // Q30, Q31, Q32, D14, D2, Q4: tko smije odobriti, smije otkazati, uz razlog

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"PENDING", "APPROVED"})
    void anaCancelsIvansRequestWithReason_Q30(String status) throws Exception {
        long id = request("ivan", status);

        HttpResponse<String> response = cancel("ana", id, REASON);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        Number cancelledById = JsonPath.read(body, "$.cancelledById");
        assertThat(JsonPath.<String>read(body, "$.status")).isEqualTo("CANCELLED");
        assertThat(JsonPath.<String>read(body, "$.cancelReason")).isEqualTo("Plans changed");
        assertThat(cancelledById.longValue()).isEqualTo(userId("ana"));
        assertThat(cancellerOf(id)).isEqualTo(userId("ana"));
    }

    @Test
    void evaCancelsSarasApprovedRequestWithReason_Q31() throws Exception {
        long id = request("sara", "APPROVED");

        HttpResponse<String> response = cancel("eva", id, REASON);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
        assertThat(cancellerOf(id)).isEqualTo(userId("eva"));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ana", "eva"})
    void approverCancelsApprovedRequestStartingTomorrow_Q32(String actor) throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-10-31",
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = cancel(actor, id, REASON);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
    }

    @Test
    void approverCancelsPastPendingRequest_Q30() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-01-05",
            "2026-01-09",
            "PENDING");

        HttpResponse<String> response = cancel("ana", id, REASON);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(statusOf(id)).isEqualTo("CANCELLED");
    }

    @Test
    void evaCancelsAnasRequestWithReason_D2() throws Exception {
        long id = request("ana", "PENDING");

        HttpResponse<String> response = cancel("eva", id, REASON);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(cancellerOf(id)).isEqualTo(userId("eva"));
    }

    @Test
    void onlyAdminCancelsRequestOfUserWithoutTeam_Q4() throws Exception {
        jdbc.update("INSERT INTO users (username, name, password_hash, hire_date)"
            + " VALUES ('nina', 'Nina', 'x', '2020-01-01')");
        long id = request("nina", "PENDING");

        HttpResponse<String> anaResponse = cancel("ana", id, REASON);
        HttpResponse<String> evaResponse = cancel("eva", id, REASON);

        assertProblem(anaResponse, 403, FORBIDDEN);
        assertThat(evaResponse.statusCode()).isEqualTo(200);
    }

    @Test
    void lukaCancelsIvanWhenIvanIsAlsoInMobile_D14() throws Exception {
        addMember("Mobile", "ivan");
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("luka", id, REASON);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // D3: tko otkazuje tuđi zahtjev, mora upisati razlog

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"", "{}", "{\"reason\":null}", "{\"reason\":\"\"}", "{\"reason\":\"   \"}"})
    void cancellingOthersRequestWithoutReasonIsBadRequest_D3(String json) throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("ana", id, json);

        assertProblem(response, 400, BLANK_REASON);
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void reasonOfOneCharacterIsAccepted_D3() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("ana", id, "{\"reason\":\"x\"}");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.cancelReason")).isEqualTo("x");
    }

    @Test
    void evaCannotCancelSarasRequestWithoutReason_D3() throws Exception {
        long id = request("sara", "APPROVED");

        HttpResponse<String> response = cancel("eva", id, "");

        assertProblem(response, 400, BLANK_REASON);
        assertThat(statusOf(id)).isEqualTo("APPROVED");
    }

    // Q30.1, Q31.1: ni voditelj ni admin ne otkazuju odobreni zahtjev koji je počeo

    @ParameterizedTest(name = "{0}, start {1}")
    @CsvSource({
        "ana, 2026-10-30",
        "ana, 2026-10-26",
        "eva, 2026-10-30",
        "eva, 2026-10-26"
    })
    void approverCannotCancelStartedApprovedRequest_Q30_1(String actor, String start) throws Exception {
        long id = insertRequest(
            "ivan",
            start,
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = cancel(actor, id, REASON);

        assertProblem(response, 409, STARTED);
        assertThat(statusOf(id)).isEqualTo("APPROVED");
    }

    // D14, D2: tko ne smije odobriti, ne smije ni otkazati

    @ParameterizedTest(name = "{0} otkazuje {1}")
    @CsvSource({
        "luka, ivan",
        "marko, ivan",
        "luka, ana",
        "ana, eva",
        "ivan, ana"
    })
    void userWhoCannotApproveCannotCancel_D14(String actor, String owner) throws Exception {
        long id = request(owner, "PENDING");

        HttpResponse<String> response = cancel(actor, id, REASON);

        assertProblem(response, 403, FORBIDDEN);
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void lukaCannotCancelAnasRequestEvenWhenAnaIsInMobile_D2() throws Exception {
        addMember("Mobile", "ana");
        long id = request("ana", "PENDING");

        HttpResponse<String> response = cancel("luka", id, REASON);

        assertProblem(response, 403, FORBIDDEN);
    }

    // R4: odbijeni i otkazani zahtjev ne mijenjaju se

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"REJECTED", "CANCELLED"})
    void finishedRequestCannotBeCancelled_R4(String status) throws Exception {
        long id = request("ivan", status);

        HttpResponse<String> response = cancel("ivan", id, "");

        assertProblem(response, 409, "Request cannot change from " + status + " to CANCELLED");
        assertThat(statusOf(id)).isEqualTo(status);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"REJECTED", "CANCELLED"})
    void approverCannotCancelFinishedRequest_R4(String status) throws Exception {
        long id = request("ivan", status);

        HttpResponse<String> response = cancel("ana", id, REASON);

        assertProblem(response, 409, "Request cannot change from " + status + " to CANCELLED");
        assertThat(statusOf(id)).isEqualTo(status);
    }

    // T3: otkazani godišnji ne troši saldo

    @Test
    void cancelledVacationNoLongerCountsInBalance_T6() throws Exception {
        long approved = insertRequest(
            "ivan",
            "2026-11-02",
            "2026-11-06",
            "APPROVED");
        long pending = insertRequest(
            "ivan",
            "2026-11-16",
            "2026-11-20",
            "PENDING");
        String token = api.tokenOf("ivan");

        HttpResponse<String> approvedResponse = cancel("ivan", approved, "");
        HttpResponse<String> pendingResponse = cancel("ivan", pending, "");
        HttpResponse<String> response = api.get("/api/me/balance", token);
        String body = response.body();

        assertThat(approvedResponse.statusCode()).isEqualTo(200);
        assertThat(pendingResponse.statusCode()).isEqualTo(200);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Integer>read(body, "$.used")).isZero();
        assertThat(JsonPath.<Integer>read(body, "$.pending")).isZero();
        assertThat(JsonPath.<Integer>read(body, "$.remaining")).isEqualTo(20);
    }

    // Oblik ulaza

    @Test
    void reasonOf500CharactersIsAccepted_T6() throws Exception {
        long id = request("ivan", "PENDING");
        String reason = "a".repeat(500);

        HttpResponse<String> response = cancel("ana", id, "{\"reason\":\"" + reason + "\"}");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void reasonOf501CharactersIsBadRequest_T6() throws Exception {
        long id = request("ivan", "PENDING");
        String reason = "a".repeat(501);

        HttpResponse<String> response = cancel("ana", id, "{\"reason\":\"" + reason + "\"}");

        assertBadField(response, "reason");
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void bodyThatIsNotJsonIsBadRequest_T6() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("ivan", id, "not json");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    @Test
    void nonNumericIdIsBadRequest_T6() throws Exception {
        String token = api.tokenOf("ivan");

        HttpResponse<String> response = api.post("/api/requests/abc/cancel", token, "");

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // Nepostojeći zahtjev, bez prijave

    @Test
    void unknownRequestIsNotFound_T6() throws Exception {
        HttpResponse<String> response = cancel("ivan", 999999, "");

        assertProblem(response, 404, NOT_FOUND);
    }

    @Test
    void negativeIdIsNotFound_T6() throws Exception {
        HttpResponse<String> response = cancel("ivan", -1, "");

        assertProblem(response, 404, NOT_FOUND);
    }

    @Test
    void cancelWithoutTokenIsUnauthorized_T6() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = api.post("/api/requests/" + id + "/cancel", null, "");

        assertProblem(response, 401, "Authentication required");
        assertThat(statusOf(id)).isEqualTo("PENDING");
    }

    // Parovi: redoslijed provjera, prva koja padne određuje odgovor

    @Test
    void tooLongReasonIsReportedBeforeUnknownRequest_T6() throws Exception {
        String reason = "a".repeat(501);

        HttpResponse<String> response = cancel("ivan", 999999, "{\"reason\":\"" + reason + "\"}");

        assertBadField(response, "reason");
    }

    @Test
    void unknownRequestIsReportedBeforePermission_T6() throws Exception {
        HttpResponse<String> response = cancel("marko", 999999, "");

        assertProblem(response, 404, NOT_FOUND);
    }

    @Test
    void permissionIsCheckedBeforeReason_T6() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> response = cancel("luka", id, "");

        assertProblem(response, 403, FORBIDDEN);
    }

    @Test
    void permissionIsCheckedBeforeStart_T6() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-10-26",
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = cancel("luka", id, REASON);

        assertProblem(response, 403, FORBIDDEN);
    }

    @Test
    void permissionIsCheckedBeforeStatus_T6() throws Exception {
        long id = request("ivan", "REJECTED");

        HttpResponse<String> response = cancel("luka", id, REASON);

        assertProblem(response, 403, FORBIDDEN);
    }

    @Test
    void reasonIsCheckedBeforeStatus_T6() throws Exception {
        long id = request("ivan", "REJECTED");

        HttpResponse<String> response = cancel("ana", id, "");

        assertProblem(response, 400, BLANK_REASON);
    }

    @Test
    void reasonIsCheckedBeforeStart_T6() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-10-26",
            "2026-11-06",
            "APPROVED");

        HttpResponse<String> response = cancel("ana", id, "");

        assertProblem(response, 400, BLANK_REASON);
    }

    // Pomoćne metode

    private HttpResponse<String> cancel(String actor, long requestId, String json) throws Exception {
        String token = api.tokenOf(actor);
        String path = "/api/requests/" + requestId + "/cancel";
        return api.post(path, token, json);
    }

    private long request(String owner, String status) {
        return insertRequest(
            owner,
            "2026-11-02",
            "2026-11-06",
            status);
    }

    private long insertRequest(
        String username,
        String start,
        String end,
        String status) {
        return jdbc.queryForObject(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " SELECT id, 'VACATION', ?, ?, ?, now() FROM users WHERE username = ? RETURNING id",
            Long.class,
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

    private long cancellerOf(long id) {
        return jdbc.queryForObject("SELECT cancelled_by_id FROM time_off_requests WHERE id = ?", Long.class, id);
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
