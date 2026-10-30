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
class CancellableApiTest {

    // Petak 30. 10. 2026. u Zagrebu: "danas" je 2026-10-30, "sutra" 2026-10-31
    private static final Instant NOW = Instant.parse("2026-10-30T10:00:00Z");

    private static final String CANCELLABLE = "/api/approvals/cancellable";

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

    // Mreža tko × čiji (L39 uvjeti 2 i 3; Q30, Q31, Q32, D2, D14): odobren zahtjev koji nije počeo

    @ParameterizedTest(name = "{0} vidi zahtjev od {1}: {2}")
    @CsvSource({
        "ivan, ivan, false",
        "ivan, marko, false",
        "ivan, sara, false",
        "ivan, ana, false",
        "ivan, eva, false",
        "marko, ivan, false",
        "ana, ivan, true",
        "ana, marko, true",
        "ana, petra, true",
        "ana, sara, false",
        "ana, tom, false",
        "ana, ana, false",
        "ana, luka, false",
        "ana, eva, false",
        "luka, sara, true",
        "luka, tom, true",
        "luka, ivan, false",
        "luka, ana, false",
        "luka, luka, false",
        "luka, eva, false",
        "eva, ivan, true",
        "eva, marko, true",
        "eva, sara, true",
        "eva, tom, true",
        "eva, ana, true",
        "eva, luka, true",
        "eva, eva, false"
    })
    void cancellableListFollowsCancelPermission_L39(String actor, String owner, boolean listed) throws Exception {
        long id = approved(owner, "2026-11-02");

        List<Long> ids = cancellableIds(actor);

        assertThat(ids.contains(id)).isEqualTo(listed);
    }

    // L39 uvjet 2, R6: svoj odobreni zahtjev nitko ne vidi na popisu, ni admin

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ivan", "ana", "eva"})
    void ownApprovedRequestIsNotListed_R6(String owner) throws Exception {
        approved(owner, "2026-11-02");

        List<Long> ids = cancellableIds(owner);

        assertThat(ids).isEmpty();
    }

    // L39 uvjet 1: samo APPROVED

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"PENDING", "REJECTED", "CANCELLED"})
    void onlyApprovedRequestsAreListed_L39(String status) throws Exception {
        insertRequest(
            "ivan",
            "2026-11-02",
            "2026-11-06",
            status);

        List<Long> ids = cancellableIds("ana");

        assertThat(ids).isEmpty();
    }

    // L39 uvjet 4, R7, Q23: nije počeo znači startDate poslije danas

    @ParameterizedTest(name = "start {0}, kraj {1}: {2}")
    @CsvSource({
        "2026-10-26, 2026-10-28, false",
        "2026-10-26, 2026-11-06, false",
        "2026-10-30, 2026-10-30, false",
        "2026-10-30, 2026-11-06, false",
        "2026-10-31, 2026-11-06, true",
        "2027-01-04, 2027-01-08, true"
    })
    void onlyRequestsThatHaveNotStartedAreListed_Q23(String start, String end, boolean listed) throws Exception {
        long id = insertRequest(
            "ivan",
            start,
            end,
            "APPROVED");

        List<Long> ids = cancellableIds("ana");

        assertThat(ids.contains(id)).isEqualTo(listed);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ana", "eva"})
    void startedRequestIsNotListedForAnyApprover_Q30_1(String actor) throws Exception {
        approved("ivan", "2026-10-30");

        List<Long> ids = cancellableIds(actor);

        assertThat(ids).isEmpty();
    }

    // Više timova: pravo dolazi iz CancelPolicy, kao u T6 (D14, D2)

    @Test
    void lukaSeesIvanWhenIvanIsAlsoInMobile_D14() throws Exception {
        addMember("Mobile", "ivan");
        long id = approved("ivan", "2026-11-02");

        List<Long> ids = cancellableIds("luka");

        assertThat(ids).containsExactly(id);
    }

    @Test
    void lukaDoesNotSeeAnaEvenWhenAnaIsInMobile_D2() throws Exception {
        addMember("Mobile", "ana");
        approved("ana", "2026-11-02");

        List<Long> ids = cancellableIds("luka");

        assertThat(ids).isEmpty();
    }

    @Test
    void onlyAdminSeesRequestOfUserWithoutTeam_Q4() throws Exception {
        jdbc.update("INSERT INTO users (username, name, password_hash, hire_date)"
            + " VALUES ('nina', 'Nina', 'x', '2020-01-01')");
        long id = approved("nina", "2026-11-02");

        List<Long> anaIds = cancellableIds("ana");
        List<Long> evaIds = cancellableIds("eva");

        assertThat(anaIds).isEmpty();
        assertThat(evaIds).containsExactly(id);
    }

    // Redoslijed i oblik odgovora

    @Test
    void listIsSortedByStartDateThenId_L39() throws Exception {
        long late = approved("ivan", "2026-11-16");
        long firstOfTwo = approved("marko", "2026-11-02");
        long secondOfTwo = approved("petra", "2026-11-02");
        approved("ana", "2026-11-09");

        List<Long> ids = cancellableIds("ana");

        assertThat(ids).containsExactly(firstOfTwo, secondOfTwo, late);
    }

    @Test
    void entryIsFullRequestWithOwnerAndApprover_L32() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-11-02",
            "2026-11-06",
            "APPROVED");
        jdbc.update(
            "UPDATE time_off_requests SET decided_by_id = (SELECT id FROM users WHERE username = 'ana'),"
                + " decided_at = now() WHERE id = ?",
            id);
        String token = api.tokenOf("ana");

        HttpResponse<String> response = api.get(CANCELLABLE, token);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$[0].ownerName")).isEqualTo("Ivan");
        assertThat(JsonPath.<String>read(body, "$[0].status")).isEqualTo("APPROVED");
        assertThat(JsonPath.<String>read(body, "$[0].decidedByName")).isEqualTo("Ana");
        assertThat(JsonPath.<String>read(body, "$[0].startDate")).isEqualTo("2026-11-02");
    }

    // Lanac s T6: nakon otkazivanja zahtjev nestane s popisa

    @Test
    void cancelledRequestLeavesTheList_Q30() throws Exception {
        long id = approved("ivan", "2026-11-02");
        String token = api.tokenOf("ana");
        List<Long> before = cancellableIds("ana");

        HttpResponse<String> response = api.post(
            "/api/requests/" + id + "/cancel",
            token,
            "{\"reason\":\"Year-end freeze\"}");
        List<Long> after = cancellableIds("ana");

        assertThat(before).containsExactly(id);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(after).isEmpty();
    }

    // Lanac s T5: odobren zahtjev se pojavi na popisu

    @Test
    void approvedRequestJoinsTheList_R1() throws Exception {
        long id = insertRequest(
            "ivan",
            "2026-11-02",
            "2026-11-06",
            "PENDING");
        String token = api.tokenOf("ana");
        List<Long> before = cancellableIds("ana");

        HttpResponse<String> response = api.post("/api/requests/" + id + "/approve", token, "");
        List<Long> after = cancellableIds("ana");

        assertThat(before).isEmpty();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(after).containsExactly(id);
    }

    // Prazna lista, bez prijave

    @Test
    void employeeGetsEmptyList_L39() throws Exception {
        approved("marko", "2026-11-02");
        String token = api.tokenOf("ivan");

        HttpResponse<String> response = api.get(CANCELLABLE, token);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        List<Object> requests = JsonPath.read(body, "$");
        assertThat(requests).isEmpty();
    }

    @Test
    void cancellableWithoutTokenIsUnauthorized_L39() throws Exception {
        HttpResponse<String> response = api.get(CANCELLABLE, null);
        String contentType = response.headers()
            .firstValue("Content-Type")
            .orElse("");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(contentType).startsWith("application/problem+json");
        assertThat(JsonPath.<String>read(body, "$.detail")).isEqualTo("Authentication required");
    }

    // Pomoćne metode

    private List<Long> cancellableIds(String actor) throws Exception {
        String token = api.tokenOf(actor);
        HttpResponse<String> response = api.get(CANCELLABLE, token);
        String body = response.body();
        assertThat(response.statusCode()).isEqualTo(200);
        List<Number> ids = JsonPath.read(body, "$[*].id");
        return ids.stream()
            .map(Number::longValue)
            .toList();
    }

    private long approved(String owner, String start) {
        return insertRequest(
            owner,
            start,
            start,
            "APPROVED");
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
}
