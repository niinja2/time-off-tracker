package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
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
class VisibilityApiTest {

    // Petak 30. 10. 2026. u Zagrebu: tekuća godina je 2026.
    private static final Instant NOW = Instant.parse("2026-10-30T10:00:00Z");

    private static final String REQUEST_FORBIDDEN = "You cannot view this request";
    private static final String BALANCE_FORBIDDEN = "You cannot view this balance";
    private static final String REQUEST_NOT_FOUND = "Request not found";
    private static final String USER_NOT_FOUND = "User not found";

    private static final List<String> EVERYONE = List.of(
        "ana",
        "ivan",
        "marko",
        "petra",
        "luka",
        "sara",
        "tom",
        "eva");

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

    // Q44–Q46: zaposlenik vidi svoje zahtjeve, u svim stanjima, sa svim detaljima

    @Test
    void ivanSeesOnlyOwnRequestsInAllStates_Q44() throws Exception {
        for (String status : List.of("PENDING", "APPROVED", "REJECTED", "CANCELLED")) {
            request("ivan", status);
        }
        request("marko", "APPROVED");

        HttpResponse<String> response = get("ivan", "/api/me/requests");
        List<Map<String, Object>> items = items(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(ownerNames(items)).containsOnly("Ivan");
        assertThat(statuses(items)).containsExactlyInAnyOrder("PENDING", "APPROVED", "REJECTED", "CANCELLED");
    }

    @Test
    void ownRequestsShowDecisionAndCancellationDetails_Q46() throws Exception {
        long rejected = request("ivan", "REJECTED");
        decide(rejected, "ana", "Busy week");
        long cancelled = insertRequest(
            "ivan",
            "SICK_LEAVE",
            "2026-12-07",
            "2026-12-11",
            "CANCELLED",
            "Flu");
        cancelBy(cancelled, "ana", "Plans changed");

        HttpResponse<String> response = get("ivan", "/api/me/requests");
        List<Map<String, Object>> items = items(response);
        Map<String, Object> rejectedItem = byId(items, rejected);
        Map<String, Object> cancelledItem = byId(items, cancelled);
        Number decidedById = (Number) rejectedItem.get("decidedById");
        Number cancelledById = (Number) cancelledItem.get("cancelledById");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(rejectedItem.get("decisionComment")).isEqualTo("Busy week");
        assertThat(decidedById.longValue()).isEqualTo(userId("ana"));
        assertThat(rejectedItem.get("reason")).isEqualTo("Trip");
        assertThat(cancelledItem.get("cancelReason")).isEqualTo("Plans changed");
        assertThat(cancelledById.longValue()).isEqualTo(userId("ana"));
        assertThat(cancelledItem.get("type")).isEqualTo("SICK_LEAVE");
    }

    @Test
    void ownRequestsAreNewestFirst_Q44() throws Exception {
        long march = insertRequest(
            "ivan",
            "VACATION",
            "2026-03-02",
            "2026-03-06",
            "PENDING",
            null);
        long novemberRejected = request("ivan", "REJECTED");
        long novemberPending = request("ivan", "PENDING");
        long june = insertRequest(
            "ivan",
            "VACATION",
            "2026-06-01",
            "2026-06-05",
            "APPROVED",
            null);

        HttpResponse<String> response = get("ivan", "/api/me/requests");
        List<Long> ids = ids(items(response));

        assertThat(ids).containsExactly(novemberPending, novemberRejected, june, march);
    }

    @Test
    void userWithoutRequestsGetsEmptyList_Q44() throws Exception {
        request("marko", "PENDING");

        HttpResponse<String> response = get("ivan", "/api/me/requests");
        List<Map<String, Object>> items = items(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(items).isEmpty();
    }

    // brief, Q16, Q17: popis zahtjeva s detaljima samo za vlasnika, voditelja tima i admina

    @ParameterizedTest(name = "{0} vidi {1}")
    @CsvSource({
        "ivan, Ivan",
        "sara, Sara",
        "tom, Tom",
        "ana, Ana Ivan Marko Petra",
        "luka, Luka Sara Tom",
        "eva, Ana Ivan Marko Petra Luka Sara Tom Eva"
    })
    void requestListShowsOnlyFullAccessOwners_brief(String viewer, String expected) throws Exception {
        for (String owner : EVERYONE) {
            request(owner, "PENDING");
        }
        String[] expectedNames = expected.split(" ");

        HttpResponse<String> response = get(viewer, "/api/requests");
        List<String> names = ownerNames(items(response));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(names).containsExactlyInAnyOrder(expectedNames);
    }

    @Test
    void anaSeesIvansRequestsInAllStatesWithTypeAndReason_brief() throws Exception {
        for (String status : List.of("PENDING", "APPROVED", "REJECTED", "CANCELLED")) {
            request("ivan", status);
        }

        HttpResponse<String> response = get("ana", "/api/requests");
        List<Map<String, Object>> items = items(response);

        assertThat(statuses(items)).containsExactlyInAnyOrder("PENDING", "APPROVED", "REJECTED", "CANCELLED");
        assertThat(items).allSatisfy(item -> {
            assertThat(item.get("type")).isEqualTo("VACATION");
            assertThat(item.get("reason")).isEqualTo("Trip");
        });
    }

    @Test
    void requestListIsNewestFirst_brief() throws Exception {
        long petraMarch = insertRequest(
            "petra",
            "VACATION",
            "2026-03-02",
            "2026-03-06",
            "APPROVED",
            null);
        long ivanNovember = request("ivan", "PENDING");
        long markoNovember = request("marko", "PENDING");
        long anaJune = insertRequest(
            "ana",
            "VACATION",
            "2026-06-01",
            "2026-06-05",
            "APPROVED",
            null);

        HttpResponse<String> response = get("ana", "/api/requests");
        List<Long> ids = ids(items(response));

        assertThat(ids).containsExactly(markoNovember, ivanNovember, anaJune, petraMarch);
    }

    @Test
    void employeeWithoutRequestsGetsEmptyRequestList_brief() throws Exception {
        request("marko", "APPROVED");

        HttpResponse<String> response = get("ivan", "/api/requests");
        List<Map<String, Object>> items = items(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(items).isEmpty();
    }

    // brief, Q16, Q17: jedan zahtjev samo uz pristup FULL (vlasnik, admin, voditelj tima vlasnika)

    @ParameterizedTest(name = "{0} gleda zahtjev od {1}: {2}")
    @CsvSource({
        "ivan, ivan, 200",
        "ana, ana, 200",
        "eva, eva, 200",
        "eva, ivan, 200",
        "eva, sara, 200",
        "eva, luka, 200",
        "ana, ivan, 200",
        "ana, petra, 200",
        "luka, sara, 200",
        "luka, tom, 200",
        "ivan, marko, 403",
        "ivan, ana, 403",
        "tom, luka, 403",
        "sara, tom, 403",
        "luka, ivan, 403",
        "luka, ana, 403",
        "ana, luka, 403",
        "ana, eva, 403",
        "ivan, eva, 403",
        "ivan, sara, 403"
    })
    void requestDetailOnlyWithFullAccess_brief(String viewer, String owner, int status) throws Exception {
        long id = request(owner, "PENDING");

        HttpResponse<String> response = get(viewer, "/api/requests/" + id);

        if (status == 403) {
            assertProblem(response, 403, REQUEST_FORBIDDEN);
            return;
        }
        String body = response.body();
        Number actualId = JsonPath.read(body, "$.id");
        Number ownerId = JsonPath.read(body, "$.ownerId");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(actualId.longValue()).isEqualTo(id);
        assertThat(ownerId.longValue()).isEqualTo(userId(owner));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    void anaReadsIvansRequestInEveryState_brief(String status) throws Exception {
        long id = request("ivan", status);

        HttpResponse<String> response = get("ana", "/api/requests/" + id);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.status")).isEqualTo(status);
        assertThat(JsonPath.<String>read(body, "$.type")).isEqualTo("VACATION");
        assertThat(JsonPath.<String>read(body, "$.reason")).isEqualTo("Trip");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"marko", "eva"})
    void unknownRequestIsNotFound_T7(String viewer) throws Exception {
        HttpResponse<String> response = get(viewer, "/api/requests/999999");

        assertProblem(response, 404, REQUEST_NOT_FOUND);
    }

    @Test
    void nonNumericRequestIdIsBadRequest_T7() throws Exception {
        HttpResponse<String> response = get("eva", "/api/requests/abc");

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // Q16, Q17: saldo drugih samo uz pristup FULL

    @ParameterizedTest(name = "{0} gleda saldo od {1}: {2}")
    @CsvSource({
        "ivan, ivan, 200",
        "ana, ana, 200",
        "eva, eva, 200",
        "eva, ivan, 200",
        "eva, tom, 200",
        "eva, luka, 200",
        "ana, ivan, 200",
        "ana, petra, 200",
        "luka, sara, 200",
        "luka, tom, 200",
        "ivan, marko, 403",
        "ivan, ana, 403",
        "tom, luka, 403",
        "sara, tom, 403",
        "luka, ivan, 403",
        "luka, ana, 403",
        "ana, sara, 403",
        "ana, luka, 403",
        "ana, eva, 403",
        "ivan, eva, 403"
    })
    void balanceOnlyWithFullAccess_Q16(String viewer, String owner, int status) throws Exception {
        HttpResponse<String> response = get(viewer, "/api/users/" + userId(owner) + "/balance");

        if (status == 403) {
            assertProblem(response, 403, BALANCE_FORBIDDEN);
            return;
        }
        String body = response.body();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Integer>read(body, "$.year")).isEqualTo(2026);
    }

    @Test
    void anaSeesIvansBalanceNumbers_Q16() throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            "APPROVED",
            null);
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-16",
            "2026-11-18",
            "PENDING",
            null);

        HttpResponse<String> response = get("ana", "/api/users/" + userId("ivan") + "/balance");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Integer>read(body, "$.entitlement")).isEqualTo(20);
        assertThat(JsonPath.<Integer>read(body, "$.used")).isEqualTo(5);
        assertThat(JsonPath.<Integer>read(body, "$.pending")).isEqualTo(3);
        assertThat(JsonPath.<Integer>read(body, "$.remaining")).isEqualTo(12);
    }

    @Test
    void evaSeesTomsProratedBalance_Q17() throws Exception {
        HttpResponse<String> response = get("eva", "/api/users/" + userId("tom") + "/balance");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Integer>read(body, "$.year")).isEqualTo(2026);
        assertThat(JsonPath.<Integer>read(body, "$.entitlement")).isEqualTo(12);
        assertThat(JsonPath.<Integer>read(body, "$.remaining")).isEqualTo(12);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ivan", "eva"})
    void unknownUserBalanceIsNotFound_T7(String viewer) throws Exception {
        HttpResponse<String> response = get(viewer, "/api/users/999999/balance");

        assertProblem(response, 404, USER_NOT_FOUND);
    }

    @Test
    void nonNumericUserIdIsBadRequest_T7() throws Exception {
        HttpResponse<String> response = get("eva", "/api/users/abc/balance");

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // Parovi: voditelj u tuđem timu (D2 je pravilo odobravanja, ne vidljivosti)

    @Test
    void lukaSeesAnaWhenAnaIsInMobileButCannotApprove_D2() throws Exception {
        addMember("Mobile", "ana");
        long id = request("ana", "PENDING");
        String token = api.tokenOf("luka");

        HttpResponse<String> list = api.get("/api/requests", token);
        HttpResponse<String> detail = api.get("/api/requests/" + id, token);
        HttpResponse<String> balance = api.get("/api/users/" + userId("ana") + "/balance", token);
        HttpResponse<String> approve = api.post("/api/requests/" + id + "/approve", token, "");

        assertThat(ownerNames(items(list))).contains("Ana");
        assertThat(detail.statusCode()).isEqualTo(200);
        assertThat(balance.statusCode()).isEqualTo(200);
        assertProblem(approve, 403, "You cannot approve or reject this request");
    }

    // Spec. 7, Q47: voditelj u timu u kojem je samo član vidi isto što i zaposlenik (test doda Anu u Mobile)

    @Test
    void anaInMobileCannotSeeSarasRequestsOrBalance_Q47() throws Exception {
        addMember("Mobile", "ana");
        long pending = request("sara", "PENDING");
        long approved = insertRequest(
            "sara",
            "VACATION",
            "2026-11-16",
            "2026-11-20",
            "APPROVED",
            null);
        String token = api.tokenOf("ana");

        HttpResponse<String> list = api.get("/api/requests", token);
        HttpResponse<String> pendingDetail = api.get("/api/requests/" + pending, token);
        HttpResponse<String> approvedDetail = api.get("/api/requests/" + approved, token);
        HttpResponse<String> balance = api.get("/api/users/" + userId("sara") + "/balance", token);

        assertThat(ownerNames(items(list))).doesNotContain("Sara");
        assertProblem(pendingDetail, 403, REQUEST_FORBIDDEN);
        assertProblem(approvedDetail, 403, REQUEST_FORBIDDEN);
        assertProblem(balance, 403, BALANCE_FORBIDDEN);
    }

    // Parovi: više zajedničkih timova, vrijedi najviši pristup

    @Test
    void saraInBothTeamsIsFullForBothLeadsAndLimitedForIvan_brief() throws Exception {
        addMember("Platform", "sara");
        long id = request("sara", "APPROVED");

        HttpResponse<String> ana = get("ana", "/api/requests/" + id);
        HttpResponse<String> luka = get("luka", "/api/requests/" + id);
        HttpResponse<String> ivan = get("ivan", "/api/requests/" + id);
        HttpResponse<String> anaList = get("ana", "/api/requests");

        assertThat(ana.statusCode()).isEqualTo(200);
        assertThat(luka.statusCode()).isEqualTo(200);
        assertProblem(ivan, 403, REQUEST_FORBIDDEN);
        assertThat(ownerNames(items(anaList))).containsExactly("Sara");
    }

    // L32, Q46, Q30, Q31: zahtjev nosi ime onoga tko je odlučio i tko je otkazao

    @Test
    void ownRequestShowsNamesOfApproverAndCanceller_Q46() throws Exception {
        long id = request("ivan", "PENDING");
        String anaToken = api.tokenOf("ana");
        String evaToken = api.tokenOf("eva");
        String approveJson = "{\"comment\":\"ok\"}";
        String cancelJson = "{\"reason\":\"Reorg\"}";

        HttpResponse<String> approve = api.post("/api/requests/" + id + "/approve", anaToken, approveJson);
        HttpResponse<String> cancel = api.post("/api/requests/" + id + "/cancel", evaToken, cancelJson);
        HttpResponse<String> mine = get("ivan", "/api/me/requests");
        String approveBody = approve.body();
        String cancelBody = cancel.body();
        Map<String, Object> item = byId(items(mine), id);

        assertThat(approve.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(approveBody, "$.decidedByName")).isEqualTo("Ana");
        assertThat(cancel.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(cancelBody, "$.decidedByName")).isEqualTo("Ana");
        assertThat(JsonPath.<String>read(cancelBody, "$.cancelledByName")).isEqualTo("Eva");
        assertThat(item).containsEntry("ownerName", "Ivan");
        assertThat(item).containsEntry("decidedByName", "Ana");
        assertThat(item).containsEntry("cancelledByName", "Eva");
    }

    @Test
    void pendingRequestHasNoDeciderOrCancellerName_L32() throws Exception {
        long id = request("ivan", "PENDING");

        HttpResponse<String> mine = get("ivan", "/api/me/requests");
        Map<String, Object> item = byId(items(mine), id);

        assertThat(item).containsEntry("decidedByName", null);
        assertThat(item).containsEntry("cancelledByName", null);
    }

    @Test
    void ownerCancellingOwnRequestShowsOwnName_R6() throws Exception {
        long id = request("ivan", "PENDING");
        String ivanToken = api.tokenOf("ivan");

        HttpResponse<String> cancel = api.post("/api/requests/" + id + "/cancel", ivanToken, "");
        HttpResponse<String> mine = api.get("/api/me/requests", ivanToken);
        Map<String, Object> item = byId(items(mine), id);

        assertThat(cancel.statusCode()).isEqualTo(200);
        assertThat(item).containsEntry("cancelledByName", "Ivan");
        assertThat(item).containsEntry("decidedByName", null);
    }

    @Test
    void rejectionShowsDeciderNameInLeadsRequestList_Q46() throws Exception {
        long id = request("ivan", "PENDING");
        String anaToken = api.tokenOf("ana");
        String rejectJson = "{\"comment\":\"Busy week\"}";

        HttpResponse<String> reject = api.post("/api/requests/" + id + "/reject", anaToken, rejectJson);
        HttpResponse<String> list = api.get("/api/requests", anaToken);
        HttpResponse<String> detail = api.get("/api/requests/" + id, anaToken);
        String rejectBody = reject.body();
        String detailBody = detail.body();
        Map<String, Object> item = byId(items(list), id);

        assertThat(reject.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(rejectBody, "$.decidedByName")).isEqualTo("Ana");
        assertThat(item).containsEntry("decidedByName", "Ana");
        assertThat(JsonPath.<String>read(detailBody, "$.decidedByName")).isEqualTo("Ana");
        assertThat(JsonPath.<String>read(detailBody, "$.cancelledByName")).isNull();
    }

    // Bez prijave

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
        "/api/me/requests",
        "/api/requests",
        "/api/requests/1",
        "/api/users/1/balance",
        "/api/calendar?month=2026-11",
        "/api/teams"
    })
    void withoutTokenIsUnauthorized_T7(String path) throws Exception {
        HttpResponse<String> response = api.get(path, null);

        assertProblem(response, 401, "Authentication required");
    }

    // Pomoćne metode

    private HttpResponse<String> get(String viewer, String path) throws Exception {
        String token = api.tokenOf(viewer);
        return api.get(path, token);
    }

    private long request(String owner, String status) {
        return insertRequest(
            owner,
            "VACATION",
            "2026-11-02",
            "2026-11-06",
            status,
            "Trip");
    }

    private long insertRequest(
        String username,
        String type,
        String start,
        String end,
        String status,
        String reason) {
        return jdbc.queryForObject(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, reason, status, created_at)"
                + " SELECT id, ?, ?, ?, ?, ?, now() FROM users WHERE username = ? RETURNING id",
            Long.class,
            type,
            LocalDate.parse(start),
            LocalDate.parse(end),
            reason,
            status,
            username);
    }

    private void decide(long requestId, String actor, String comment) {
        jdbc.update(
            "UPDATE time_off_requests SET decision_comment = ?, decided_at = now(),"
                + " decided_by_id = (SELECT id FROM users WHERE username = ?) WHERE id = ?",
            comment,
            actor,
            requestId);
    }

    private void cancelBy(long requestId, String actor, String reason) {
        jdbc.update(
            "UPDATE time_off_requests SET cancel_reason = ?, cancelled_at = now(),"
                + " cancelled_by_id = (SELECT id FROM users WHERE username = ?) WHERE id = ?",
            reason,
            actor,
            requestId);
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
            + " WHERE (team_id = (SELECT id FROM teams WHERE name = 'Mobile')"
            + " AND user_id = (SELECT id FROM users WHERE username = 'ana'))"
            + " OR (team_id = (SELECT id FROM teams WHERE name = 'Platform')"
            + " AND user_id = (SELECT id FROM users WHERE username = 'sara'))");
    }

    private long userId(String username) {
        return jdbc.queryForObject("SELECT id FROM users WHERE username = ?", Long.class, username);
    }

    private List<Map<String, Object>> items(HttpResponse<String> response) {
        String body = response.body();
        return JsonPath.read(body, "$");
    }

    private Map<String, Object> byId(List<Map<String, Object>> items, long id) {
        return items.stream()
            .filter(item -> ((Number) item.get("id")).longValue() == id)
            .findFirst()
            .orElseThrow();
    }

    private List<Long> ids(List<Map<String, Object>> items) {
        return items.stream()
            .map(item -> ((Number) item.get("id")).longValue())
            .toList();
    }

    private List<String> ownerNames(List<Map<String, Object>> items) {
        return items.stream()
            .map(item -> (String) item.get("ownerName"))
            .toList();
    }

    private List<String> statuses(List<Map<String, Object>> items) {
        return items.stream()
            .map(item -> (String) item.get("status"))
            .toList();
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
}
