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
class CalendarApiTest {

    private static final Instant NOW = Instant.parse("2026-10-30T10:00:00Z");

    private static final String NOVEMBER = "/api/calendar?month=2026-11";

    private static final String[] FULL_KEYS = {"name", "startDate", "endDate", "type", "status"};
    private static final String[] LIMITED_KEYS = {"name", "startDate", "endDate"};

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

    // Q41: svoje zahtjeve na čekanju i odobrene korisnik vidi s tipom i stanjem

    @Test
    void ivanSeesOwnPendingAndApprovedWithTypeAndStatus_Q41() throws Exception {
        insertRequest(
            "ivan",
            "SICK_LEAVE",
            "2026-11-02",
            "2026-11-06",
            "PENDING");
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-16",
            "2026-11-20",
            "APPROVED");

        HttpResponse<String> response = get("ivan", NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(entries).hasSize(2);
        Map<String, Object> pending = entries.get(0);
        Map<String, Object> approved = entries.get(1);
        assertThat(pending.keySet()).containsExactlyInAnyOrder(FULL_KEYS);
        assertThat(pending)
            .containsEntry("name", "Ivan")
            .containsEntry("startDate", "2026-11-02")
            .containsEntry("endDate", "2026-11-06")
            .containsEntry("type", "SICK_LEAVE")
            .containsEntry("status", "PENDING");
        assertThat(approved)
            .containsEntry("type", "VACATION")
            .containsEntry("status", "APPROVED");
    }

    // brief, Q47: kolege iz tima samo odobrene, samo ime i datumi

    @Test
    void ivanSeesColleaguesApprovedWithoutTypeAndStatus_Q47() throws Exception {
        insertRequest(
            "marko",
            "SICK_LEAVE",
            "2026-11-09",
            "2026-11-10",
            "APPROVED");
        insertRequest(
            "marko",
            "VACATION",
            "2026-11-23",
            "2026-11-24",
            "PENDING");
        insertRequest(
            "ana",
            "VACATION",
            "2026-11-02",
            "2026-11-03",
            "APPROVED");
        insertRequest(
            "ana",
            "VACATION",
            "2026-11-16",
            "2026-11-17",
            "PENDING");

        HttpResponse<String> response = get("ivan", NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(names(entries)).containsExactly("Ana", "Marko");
        Map<String, Object> ana = entries.get(0);
        Map<String, Object> marko = entries.get(1);
        assertThat(ana.keySet()).containsExactlyInAnyOrder(LIMITED_KEYS);
        assertThat(marko.keySet()).containsExactlyInAnyOrder(LIMITED_KEYS);
        assertThat(marko)
            .containsEntry("startDate", "2026-11-09")
            .containsEntry("endDate", "2026-11-10");
    }

    @Test
    void ivanDoesNotSeeOtherTeamOrUserWithoutTeam_brief() throws Exception {
        for (String owner : List.of("luka", "sara", "tom", "eva")) {
            request(owner, "APPROVED");
        }

        HttpResponse<String> response = get("ivan", NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(entries).isEmpty();
    }

    // Q48: odbijeni i otkazani nikad nisu na kalendaru

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ivan", "marko", "ana", "eva"})
    void rejectedAndCancelledAreNeverOnCalendar_Q48(String viewer) throws Exception {
        request("ivan", "REJECTED");
        insertRequest(
            "ivan",
            "VACATION",
            "2026-11-16",
            "2026-11-20",
            "CANCELLED");

        HttpResponse<String> response = get(viewer, NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(entries).isEmpty();
    }

    // Q42: voditelj vidi članove timova koje vodi, na čekanju i odobrene, s tipom

    @Test
    void anaSeesIvansPendingAndApprovedWithType_Q42() throws Exception {
        request("ivan", "PENDING");
        insertRequest(
            "ivan",
            "DAY_OFF",
            "2026-11-16",
            "2026-11-16",
            "APPROVED");
        request("luka", "APPROVED");

        HttpResponse<String> response = get("ana", NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(names(entries)).containsExactly("Ivan", "Ivan");
        assertThat(entries).allSatisfy(entry -> assertThat(entry.keySet()).containsExactlyInAnyOrder(FULL_KEYS));
        assertThat(types(entries)).containsExactly("VACATION", "DAY_OFF");
        assertThat(statuses(entries)).containsExactly("PENDING", "APPROVED");
    }

    @Test
    void lukaSeesSaraAndTomPendingAndApprovedWithType_Q42() throws Exception {
        request("sara", "PENDING");
        insertRequest(
            "sara",
            "VACATION",
            "2026-11-16",
            "2026-11-20",
            "APPROVED");
        request("tom", "APPROVED");
        insertRequest(
            "tom",
            "VACATION",
            "2026-11-23",
            "2026-11-24",
            "PENDING");
        request("ana", "APPROVED");

        HttpResponse<String> response = get("luka", NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(names(entries)).containsExactlyInAnyOrder("Sara", "Sara", "Tom", "Tom");
        assertThat(statuses(entries)).containsExactlyInAnyOrder("PENDING", "APPROVED", "APPROVED", "PENDING");
        assertThat(entries).allSatisfy(entry -> assertThat(entry.keySet()).containsExactlyInAnyOrder(FULL_KEYS));
    }

    // Q43: admin vidi sve u tvrtki, i korisnike bez tima

    @Test
    void evaSeesEveryonesPendingIncludingHerOwn_Q43() throws Exception {
        for (String owner : EVERYONE) {
            request(owner, "PENDING");
        }

        HttpResponse<String> response = get("eva", NOVEMBER);
        List<Map<String, Object>> entries = entries(response);

        assertThat(names(entries))
            .containsExactlyInAnyOrder("Ana", "Ivan", "Marko", "Petra", "Luka", "Sara", "Tom", "Eva");
        assertThat(entries).allSatisfy(entry -> assertThat(entry.keySet()).containsExactlyInAnyOrder(FULL_KEYS));
    }

    // Redoslijed: po startDate, pa po imenu

    @Test
    void calendarIsSortedByStartDateThenName_T7() throws Exception {
        insertRequest(
            "ana",
            "VACATION",
            "2026-11-09",
            "2026-11-10",
            "APPROVED");
        request("petra", "APPROVED");
        request("ivan", "APPROVED");
        insertRequest(
            "tom",
            "VACATION",
            "2026-10-29",
            "2026-11-03",
            "APPROVED");

        HttpResponse<String> response = get("eva", NOVEMBER);
        List<String> names = names(entries(response));

        assertThat(names).containsExactly("Tom", "Ivan", "Petra", "Ana");
    }

    // Raspon: zahtjev je u mjesecu ako mu barem jedan dan pada u mjesec (oba kraja uključiva)

    @ParameterizedTest(name = "{0} do {1} u {2}: {3}")
    @CsvSource({
        "2026-10-26, 2026-10-30, 2026-11, false",
        "2026-10-26, 2026-11-01, 2026-11, true",
        "2026-10-29, 2026-11-03, 2026-10, true",
        "2026-10-29, 2026-11-03, 2026-11, true",
        "2026-11-02, 2026-11-06, 2026-11, true",
        "2026-10-05, 2026-12-04, 2026-11, true",
        "2026-11-30, 2026-12-04, 2026-11, true",
        "2026-12-01, 2026-12-04, 2026-11, false"
    })
    void requestIsInMonthWhenAnyDayFallsInIt_T7(String start, String end, String month, boolean shown) throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            start,
            end,
            "APPROVED");

        HttpResponse<String> response = get("ivan", "/api/calendar?month=" + month);
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(entries).hasSize(shown ? 1 : 0);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"2026-10", "2026-11"})
    void requestAcrossMonthsKeepsWholeRange_T7(String month) throws Exception {
        insertRequest(
            "ivan",
            "VACATION",
            "2026-10-29",
            "2026-11-03",
            "APPROVED");

        HttpResponse<String> response = get("ivan", "/api/calendar?month=" + month);
        List<Map<String, Object>> entries = entries(response);

        assertThat(entries).hasSize(1);
        assertThat(entries.get(0))
            .containsEntry("startDate", "2026-10-29")
            .containsEntry("endDate", "2026-11-03");
    }

    // Q43, D12: filtar po timu sužava popis, vidljivost se ne mijenja

    @Test
    void evaFiltersByPlatform_Q43() throws Exception {
        for (String owner : EVERYONE) {
            request(owner, "APPROVED");
        }

        HttpResponse<String> response = get("eva", NOVEMBER + "&teamId=" + teamId("Platform"));
        List<String> names = names(entries(response));

        assertThat(names).containsExactlyInAnyOrder("Ana", "Ivan", "Marko", "Petra");
    }

    @Test
    void ivanFilteringByMobileGetsEmptyList_T7() throws Exception {
        for (String owner : EVERYONE) {
            request(owner, "APPROVED");
        }

        HttpResponse<String> response = get("ivan", NOVEMBER + "&teamId=" + teamId("Mobile"));
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(entries).isEmpty();
    }

    @Test
    void unknownTeamGivesEmptyList_T7() throws Exception {
        for (String owner : EVERYONE) {
            request(owner, "APPROVED");
        }

        HttpResponse<String> response = get("eva", NOVEMBER + "&teamId=999999");
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(entries).isEmpty();
    }

    @Test
    void teamFilterDoesNotChangeVisibility_Q47() throws Exception {
        request("ivan", "PENDING");
        insertRequest(
            "marko",
            "VACATION",
            "2026-11-09",
            "2026-11-10",
            "APPROVED");
        insertRequest(
            "marko",
            "VACATION",
            "2026-11-23",
            "2026-11-24",
            "PENDING");

        HttpResponse<String> response = get("ivan", NOVEMBER + "&teamId=" + teamId("Platform"));
        List<Map<String, Object>> entries = entries(response);

        assertThat(names(entries)).containsExactly("Ivan", "Marko");
        assertThat(entries.get(0).keySet()).containsExactlyInAnyOrder(FULL_KEYS);
        assertThat(entries.get(1).keySet()).containsExactlyInAnyOrder(LIMITED_KEYS);
    }

    // Parovi: član više timova (test doda Saru u Platform)

    @Test
    void saraInBothTeamsAppearsOnceWithHighestAccess_brief() throws Exception {
        addMember("Platform", "sara");
        request("sara", "APPROVED");

        HttpResponse<String> ivan = get("ivan", NOVEMBER);
        HttpResponse<String> ana = get("ana", NOVEMBER);
        List<Map<String, Object>> ivanEntries = entries(ivan);
        List<Map<String, Object>> anaEntries = entries(ana);

        assertThat(names(ivanEntries)).containsExactly("Sara");
        assertThat(ivanEntries.get(0).keySet()).containsExactlyInAnyOrder(LIMITED_KEYS);
        assertThat(names(anaEntries)).containsExactly("Sara");
        assertThat(anaEntries.get(0).keySet()).containsExactlyInAnyOrder(FULL_KEYS);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"Platform", "Mobile"})
    void saraInBothTeamsIsInEachTeamFilter_brief(String team) throws Exception {
        addMember("Platform", "sara");
        request("sara", "APPROVED");

        HttpResponse<String> response = get("eva", NOVEMBER + "&teamId=" + teamId(team));
        List<String> names = names(entries(response));

        assertThat(names).containsExactly("Sara");
    }

    // Parovi: voditelj u tuđem timu (test doda Anu u Mobile); D2 je pravilo odobravanja, ne vidljivosti

    @Test
    void lukaSeesAnaFullWhenAnaIsInMobile_D2() throws Exception {
        addMember("Mobile", "ana");
        request("ana", "PENDING");
        insertRequest(
            "ana",
            "VACATION",
            "2026-11-16",
            "2026-11-20",
            "APPROVED");

        HttpResponse<String> luka = get("luka", NOVEMBER);
        HttpResponse<String> sara = get("sara", NOVEMBER);
        List<Map<String, Object>> lukaEntries = entries(luka);
        List<Map<String, Object>> saraEntries = entries(sara);

        assertThat(statuses(lukaEntries)).containsExactly("PENDING", "APPROVED");
        assertThat(names(saraEntries)).containsExactly("Ana");
        assertThat(saraEntries.get(0).keySet()).containsExactlyInAnyOrder(LIMITED_KEYS);
    }

    // Spec. 7, Q47: voditelj u timu u kojem je samo član vidi isto što i zaposlenik (test doda Anu u Mobile)

    @Test
    void anaInMobileSeesSaraOnlyApprovedWithoutTypeAndStatus_Q47() throws Exception {
        addMember("Mobile", "ana");
        request("sara", "PENDING");
        insertRequest(
            "sara",
            "VACATION",
            "2026-11-16",
            "2026-11-20",
            "APPROVED");
        insertRequest(
            "tom",
            "VACATION",
            "2026-11-23",
            "2026-11-24",
            "PENDING");

        HttpResponse<String> response = get("ana", NOVEMBER + "&teamId=" + teamId("Mobile"));
        List<Map<String, Object>> entries = entries(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(names(entries)).containsExactly("Sara");
        assertThat(entries.get(0).keySet()).containsExactlyInAnyOrder(LIMITED_KEYS);
        assertThat(entries.get(0))
            .containsEntry("startDate", "2026-11-16")
            .containsEntry("endDate", "2026-11-20");
    }

    // Oblik ulaza

    @ParameterizedTest(name = "[{0}]")
    @ValueSource(strings = {"", "?month=", "?month=2026-13", "?month=november", "?month=2026-11-01"})
    void missingOrInvalidMonthIsBadRequest_T7(String query) throws Exception {
        HttpResponse<String> response = get("ivan", "/api/calendar" + query);

        assertBadField(response, "month");
    }

    @Test
    void nonNumericTeamIdIsBadRequest_T7() throws Exception {
        HttpResponse<String> response = get("eva", NOVEMBER + "&teamId=abc");

        assertBadField(response, "teamId");
    }

    // Q3, Q43, D12: timovi za filtar; admin sve, ostali svoje, poredano po imenu

    @ParameterizedTest(name = "{0}: {1}")
    @CsvSource({
        "eva, Mobile Platform",
        "ana, Platform",
        "ivan, Platform",
        "luka, Mobile",
        "sara, Mobile",
        "tom, Mobile"
    })
    void teamsForFilter_Q43(String viewer, String expected) throws Exception {
        String[] expectedNames = expected.split(" ");

        HttpResponse<String> response = get(viewer, "/api/teams");
        List<String> names = names(entries(response));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(names).containsExactly(expectedNames);
    }

    @Test
    void teamCarriesIdAndName_T7() throws Exception {
        HttpResponse<String> response = get("ivan", "/api/teams");
        List<Map<String, Object>> teams = entries(response);
        Map<String, Object> platform = teams.get(0);
        Number id = (Number) platform.get("id");

        assertThat(platform).containsEntry("name", "Platform");
        assertThat(id.longValue()).isEqualTo(teamId("Platform"));
    }

    @Test
    void anaInMobileGetsBothTeams_Q3() throws Exception {
        addMember("Mobile", "ana");

        HttpResponse<String> response = get("ana", "/api/teams");
        List<String> names = names(entries(response));

        assertThat(names).containsExactly("Mobile", "Platform");
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
            status);
    }

    private long insertRequest(
        String username,
        String type,
        String start,
        String end,
        String status) {
        return jdbc.queryForObject(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, reason, status, created_at)"
                + " SELECT id, ?, ?, ?, 'Secret reason', ?, now() FROM users WHERE username = ? RETURNING id",
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
            + " WHERE (team_id = (SELECT id FROM teams WHERE name = 'Mobile')"
            + " AND user_id = (SELECT id FROM users WHERE username = 'ana'))"
            + " OR (team_id = (SELECT id FROM teams WHERE name = 'Platform')"
            + " AND user_id = (SELECT id FROM users WHERE username = 'sara'))");
    }

    private long teamId(String name) {
        return jdbc.queryForObject("SELECT id FROM teams WHERE name = ?", Long.class, name);
    }

    private List<Map<String, Object>> entries(HttpResponse<String> response) {
        String body = response.body();
        return JsonPath.read(body, "$");
    }

    private List<String> names(List<Map<String, Object>> entries) {
        return entries.stream()
            .map(entry -> (String) entry.get("name"))
            .toList();
    }

    private List<String> types(List<Map<String, Object>> entries) {
        return entries.stream()
            .map(entry -> (String) entry.get("type"))
            .toList();
    }

    private List<String> statuses(List<Map<String, Object>> entries) {
        return entries.stream()
            .map(entry -> (String) entry.get("status"))
            .toList();
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
