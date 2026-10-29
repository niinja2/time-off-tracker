package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class BalanceApiNewYearTest {

    @TestConfiguration(proxyBeanMethods = false)
    static class NewYearInZagrebClockConfiguration {

        @Bean
        @Primary
        Clock fixedClock() {
            Instant now = Instant.parse("2026-12-31T23:30:00Z");
            return Clock.fixed(now, ZoneId.of("Europe/Zagreb"));
        }
    }

    @Value("${local.server.port}")
    private int port;

    @Test
    void currentYearComesFromClockInZagrebTime_D8() throws Exception {
        ApiClient api = new ApiClient(port);
        String token = api.tokenOf("tom");
        String body = api.get("/api/me/balance", token).body();

        assertThat(JsonPath.<Integer>read(body, "$.year")).isEqualTo(2027);
        assertThat(JsonPath.<Integer>read(body, "$.entitlement")).isEqualTo(20);
    }
}
