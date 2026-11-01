package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * T2 Ključ: šifra za tokene ima najmanje 32 bajta, inače se aplikacija ne pokrene.
 *
 * Izvori: sučelje T2 (Token, Ključ: najmanje 32 bajta, bajtovi teksta u UTF-8); L15, L50.
 * Mreža: duljina šifre u bajtovima (31, 32); znakovi i bajtovi (31 znak od kojih je jedan
 *   "č", dakle 32 bajta).
 * Pokriva: T2
 */
class JwtSecretLengthTest {

    // L50: 31 bajt je premalo; aplikacija stane pri pokretanju i kaže koliko treba i koliko je zadano
    @Test
    void secretOf31BytesStopsStartup_T2() {
        String secret = "a".repeat(31);

        Throwable thrown = catchThrowable(() -> start(secret).close());
        List<String> messages = messages(thrown);

        assertThat(thrown).isNotNull();
        assertThat(messages).anyMatch(message -> message.contains("32") && message.contains("31"));
    }

    // L50: točno 32 bajta je dovoljno; broje se bajtovi u UTF-8, ne znakovi
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaač"})
    void secretOf32BytesStartsApplication_T2(String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);

        assertThat(bytes).hasSize(32);
        try (ConfigurableApplicationContext context = start(secret)) {
            assertThat(context.isActive()).isTrue();
        }
    }

    // Pomoćne metode

    private static ConfigurableApplicationContext start(String secret) {
        return SpringApplication.from(TimeoffApplication::main)
            .with(TestcontainersConfiguration.class)
            .run("--app.jwt.secret=" + secret, "--server.port=0")
            .getApplicationContext();
    }

    // Poruke iznimke i svih njenih uzroka, bez praznih
    private static List<String> messages(Throwable thrown) {
        return Stream.iterate(thrown, Objects::nonNull, Throwable::getCause)
            .map(Throwable::getMessage)
            .filter(Objects::nonNull)
            .toList();
    }
}
