package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
class BalanceApiTest {

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClockConfiguration {

        @Bean
        @Primary
        Clock fixedClock() {
            Instant now = Instant.parse("2026-10-30T10:00:00Z");
            return Clock.fixed(now, ZoneId.of("Europe/Zagreb"));
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
    }

    @Test
    void tomSeesProportionalBalanceForCurrentYear_Q15() throws Exception {
        HttpResponse<String> response = balanceOf("tom");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Integer>read(body, "$.year")).isEqualTo(2026);
        assertThat(JsonPath.<Integer>read(body, "$.entitlement")).isEqualTo(12);
        assertThat(JsonPath.<Integer>read(body, "$.used")).isZero();
        assertThat(JsonPath.<Integer>read(body, "$.pending")).isZero();
        assertThat(JsonPath.<Integer>read(body, "$.remaining")).isEqualTo(12);
    }

    @Test
    void ivanSeesFullBalanceForCurrentYear_R11() throws Exception {
        String body = balanceOf("ivan").body();

        assertThat(JsonPath.<Integer>read(body, "$.year")).isEqualTo(2026);
        assertThat(JsonPath.<Integer>read(body, "$.entitlement")).isEqualTo(20);
        assertThat(JsonPath.<Integer>read(body, "$.remaining")).isEqualTo(20);
    }

    @Test
    void balanceCountsOnlyOwnRequests_R12() throws Exception {
        jdbc.update(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " SELECT id, 'VACATION', ?, ?, 'APPROVED', now() FROM users WHERE username = 'ivan'",
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6));
        try {
            String ivan = balanceOf("ivan").body();
            String marko = balanceOf("marko").body();

            assertThat(JsonPath.<Integer>read(ivan, "$.used")).isEqualTo(5);
            assertThat(JsonPath.<Integer>read(ivan, "$.remaining")).isEqualTo(15);
            assertThat(JsonPath.<Integer>read(marko, "$.used")).isZero();
            assertThat(JsonPath.<Integer>read(marko, "$.remaining")).isEqualTo(20);
        } finally {
            jdbc.update("DELETE FROM time_off_requests"
                + " WHERE owner_id = (SELECT id FROM users WHERE username = 'ivan')");
        }
    }

    @Test
    void balanceWithoutTokenIsUnauthorized_T3() throws Exception {
        HttpResponse<String> response = api.get("/api/me/balance", null);
        String contentType = response.headers()
            .firstValue("Content-Type")
            .orElse("");
        String body = response.body();
        String detail = JsonPath.read(body, "$.detail");

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(contentType).startsWith("application/problem+json");
        assertThat(detail).isEqualTo("Authentication required");
    }

    private HttpResponse<String> balanceOf(String username) throws Exception {
        String token = api.tokenOf(username);
        return api.get("/api/me/balance", token);
    }
}
