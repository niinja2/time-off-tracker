package com.example.timeoff.security;

import com.example.timeoff.domain.User;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

// The token carries identity only; roles and teams are read from the database on every call (L9, D6).
@Service
class TokenService {

    private final JwtEncoder jwtEncoder;
    private final Duration ttl;

    TokenService(JwtEncoder jwtEncoder, @Value("${app.jwt.ttl}") Duration ttl) {
        this.jwtEncoder = jwtEncoder;
        this.ttl = ttl;
    }

    String issue(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(ttl);
        String subject = String.valueOf(user.getId());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .claim("username", user.getUsername())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        return jwtEncoder.encode(parameters)
                .getTokenValue();
    }
}
