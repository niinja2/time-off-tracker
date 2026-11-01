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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * T6 Otkazivanje: "danas" je datum u Zagrebu, ne u UTC-u.
 *
 * Izvori: specifikacija 4; sučelje T6 (provjera 5, "danas" je LocalDate.now(clock)) i T3 (Današnji datum).
 * Mreža: sat 2026-10-30 23:30 UTC, što je u Zagrebu već 31. 10. 00:30; početak 31. 10. i 1. 11.
 * Pokriva: Q23, Q24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class CancelDayBoundaryApiTest {

    // U UTC-u je još 30. 10., u Zagrebu (CET, UTC+1) je 31. 10.
    private static final Instant NOW = Instant.parse("2026-10-30T23:30:00Z");

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

    // Q23, Q24: zahtjev koji počinje danas je počeo; danas se računa u zoni sata (Zagreb)

    @Test
    void requestStartingOnZagrebTodayHasStarted_Q23() throws Exception {
        long id = approved("2026-10-31");

        HttpResponse<String> response = cancel(id);
        String detail = JsonPath.read(response.body(), "$.detail");

        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(detail).isEqualTo("Request has already started");
    }

    @Test
    void requestStartingOnZagrebTomorrowCanBeCancelled_Q23() throws Exception {
        long id = approved("2026-11-01");

        HttpResponse<String> response = cancel(id);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // Pomoćne metode

    private HttpResponse<String> cancel(long id) throws Exception {
        String token = api.tokenOf("ivan");
        return api.post("/api/requests/" + id + "/cancel", token, "");
    }

    private long approved(String start) {
        return jdbc.queryForObject(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " SELECT id, 'VACATION', ?, ?, 'APPROVED', now() FROM users WHERE username = 'ivan' RETURNING id",
            Long.class,
            LocalDate.parse(start),
            LocalDate.parse("2026-11-06"));
    }
}
