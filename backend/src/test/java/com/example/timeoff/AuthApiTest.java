package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.timeoff.domain.UserRepository;
import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class AuthApiTest {

    private static final String INVALID_LOGIN = "Invalid username or password";
    private static final String AUTH_REQUIRED = "Authentication required";
    private static final String INVALID_TOKEN = "Invalid or expired token";
    private static final String WRONG_SECRET = "wrong-secret-wrong-secret-wrong-secret-0123";

    private final HttpClient http = HttpClient.newHttpClient();

    @Value("${local.server.port}")
    private int port;

    @Value("${app.jwt.secret}")
    private String secret;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "ana, Ana, false, true",
        "ivan, Ivan, false, false",
        "marko, Marko, false, false",
        "petra, Petra, false, false",
        "luka, Luka, false, true",
        "sara, Sara, false, false",
        "tom, Tom, false, false",
        "eva, Eva, true, false"
    })
    void seededUserLogsInWithPassword_D16(
        String username,
        String name,
        boolean admin,
        boolean teamLead) throws Exception {
        HttpResponse<String> response = login(username, "password");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.token")).isNotBlank();
        assertThat(JsonPath.<Number>read(body, "$.user.id").longValue()).isEqualTo(idOf(username));
        assertThat(JsonPath.<String>read(body, "$.user.username")).isEqualTo(username);
        assertThat(JsonPath.<String>read(body, "$.user.name")).isEqualTo(name);
        assertThat(JsonPath.<Boolean>read(body, "$.user.admin")).isEqualTo(admin);
        assertThat(JsonPath.<Boolean>read(body, "$.user.teamLead")).isEqualTo(teamLead);
    }

    @Test
    void loginWithWrongPasswordIsUnauthorized_T2() throws Exception {
        HttpResponse<String> response = login("ana", "wrong");

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_LOGIN);
    }

    @Test
    void loginWithUnknownUserGivesSameErrorAsWrongPassword_T2() throws Exception {
        HttpResponse<String> response = login("nobody", "password");

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_LOGIN);
    }

    @Test
    void loginUsernameIsCaseSensitive_T2() throws Exception {
        HttpResponse<String> response = login("Ana", "password");

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_LOGIN);
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = {
        "{\"password\":\"password\"} | username",
        "{\"username\":\"ana\"} | password",
        "{\"username\":\"\",\"password\":\"password\"} | username",
        "{\"username\":\"ana\",\"password\":\"\"} | password"
    })
    void loginWithMissingOrEmptyFieldIsBadRequest_T2(String body, String field) throws Exception {
        HttpResponse<String> response = post("/api/auth/login", body);

        assertThat(problemDetail(response, 400)).contains(field);
    }

    @Test
    void loginTokenCarriesIdentityClaims_T2() throws Exception {
        SignedJWT jwt = SignedJWT.parse(tokenOf("ivan"));
        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        Instant issuedAt = claims.getIssueTime().toInstant();
        Instant expiresAt = claims.getExpirationTime().toInstant();
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        MACVerifier verifier = new MACVerifier(secretBytes);
        String ivanId = String.valueOf(idOf("ivan"));

        assertThat(jwt.getHeader().getAlgorithm()).isEqualTo(JWSAlgorithm.HS256);
        assertThat(jwt.verify(verifier)).isTrue();
        assertThat(claims.getSubject()).isEqualTo(ivanId);
        assertThat(claims.getStringClaim("username")).isEqualTo("ivan");
        assertThat(Duration.between(issuedAt, expiresAt)).isEqualTo(Duration.ofHours(8));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ivan", "ana", "eva"})
    void meReturnsOwnerOfLoginToken_T2(String username) throws Exception {
        HttpResponse<String> response = me(tokenOf(username));
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.username")).isEqualTo(username);
        assertThat(JsonPath.<Number>read(body, "$.id").longValue()).isEqualTo(idOf(username));
    }

    @Test
    void meWithoutTokenIsUnauthorized_T2() throws Exception {
        HttpResponse<String> response = get("/api/me", null);

        assertThat(problemDetail(response, 401)).isEqualTo(AUTH_REQUIRED);
    }

    @Test
    void otherApiRouteWithoutTokenIsUnauthorized_T2() throws Exception {
        HttpResponse<String> response = get("/api/requests", null);

        assertThat(problemDetail(response, 401)).isEqualTo(AUTH_REQUIRED);
    }

    @Test
    void healthIsPublic_T2() throws Exception {
        assertThat(get("/actuator/health", null).statusCode()).isEqualTo(200);
    }

    @Test
    void tokenSignedWithAppSecretIsAccepted_T2() throws Exception {
        String token = sign(
            secret,
            String.valueOf(idOf("ivan")),
            "ivan",
            Instant.now().plus(Duration.ofHours(1)));

        HttpResponse<String> response = me(token);
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(body, "$.username")).isEqualTo("ivan");
    }

    @Test
    void tokenWithWrongSignatureIsUnauthorized_T2() throws Exception {
        String token = sign(
            WRONG_SECRET,
            String.valueOf(idOf("ivan")),
            "ivan",
            Instant.now().plus(Duration.ofHours(1)));
        HttpResponse<String> response = me(token);

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_TOKEN);
    }

    @Test
    void expiredTokenIsUnauthorized_T2() throws Exception {
        String token = sign(
            secret,
            String.valueOf(idOf("ivan")),
            "ivan",
            Instant.now().minus(Duration.ofHours(1)));
        HttpResponse<String> response = me(token);

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_TOKEN);
    }

    @Test
    void malformedTokenIsUnauthorized_T2() throws Exception {
        HttpResponse<String> response = me("not-a-jwt");

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_TOKEN);
    }

    @Test
    void tokenWithNonNumericSubjectIsUnauthorized_T2() throws Exception {
        String token = sign(
            secret,
            "ivan",
            "ivan",
            Instant.now().plus(Duration.ofHours(1)));
        HttpResponse<String> response = me(token);

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_TOKEN);
    }

    @Test
    void tokenOfUserMissingFromDatabaseIsUnauthorized_T2() throws Exception {
        String token = sign(
            secret,
            "999999",
            "ghost",
            Instant.now().plus(Duration.ofHours(1)));
        HttpResponse<String> response = me(token);

        assertThat(problemDetail(response, 401)).isEqualTo(INVALID_TOKEN);
    }

    @Test
    void adminFlagChangedInDatabaseAppliesWithoutNewLogin_D6() throws Exception {
        String token = tokenOf("marko");
        jdbc.update("UPDATE users SET admin = true WHERE username = 'marko'");
        try {
            HttpResponse<String> response = me(token);
            String body = response.body();

            assertThat(JsonPath.<Boolean>read(body, "$.admin")).isTrue();
        } finally {
            jdbc.update("UPDATE users SET admin = false WHERE username = 'marko'");
        }
    }

    private HttpResponse<String> login(String username, String password) throws Exception {
        return post(
            "/api/auth/login",
            "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
    }

    private String tokenOf(String username) throws Exception {
        return JsonPath.read(login(username, "password").body(), "$.token");
    }

    private HttpResponse<String> me(String token) throws Exception {
        return get("/api/me", "Bearer " + token);
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path, String authorization) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
            .GET();
        if (authorization != null) {
            builder.header("Authorization", authorization);
        }
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private String problemDetail(HttpResponse<String> response, int status) {
        String contentType = response.headers()
            .firstValue("Content-Type")
            .orElse("");
        String body = response.body();

        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(contentType).startsWith("application/problem+json");
        assertThat(JsonPath.<Integer>read(body, "$.status")).isEqualTo(status);
        assertThat(JsonPath.<String>read(body, "$.title")).isNotBlank();
        return JsonPath.read(body, "$.detail");
    }

    private long idOf(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow()
            .getId();
    }

    private String sign(
        String key,
        String subject,
        String username,
        Instant expiresAt) throws JOSEException {
        Instant issuedAt = expiresAt.minus(Duration.ofHours(8));
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(subject)
            .claim("username", username)
            .issueTime(Date.from(issuedAt))
            .expirationTime(Date.from(expiresAt))
            .build();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT jwt = new SignedJWT(header, claims);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        MACSigner signer = new MACSigner(keyBytes);
        jwt.sign(signer);
        return jwt.serialize();
    }
}
