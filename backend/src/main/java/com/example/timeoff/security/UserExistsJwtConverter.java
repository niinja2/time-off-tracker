package com.example.timeoff.security;

import com.example.timeoff.domain.UserRepository;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

// A token is accepted only if sub is a number and that user still exists in the database.
@Component
class UserExistsJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    UserExistsJwtConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String subject = jwt.getSubject();
        Long userId = parseUserId(subject);

        if (userId == null || !userRepository.existsById(userId)) {
            throw new InvalidBearerTokenException(ProblemAuthenticationEntryPoint.INVALID_TOKEN);
        }

        return new JwtAuthenticationToken(jwt, List.of(), subject);
    }

    private Long parseUserId(String subject) {
        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
