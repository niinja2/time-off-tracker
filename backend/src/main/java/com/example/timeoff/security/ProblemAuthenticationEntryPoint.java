package com.example.timeoff.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

// Writes every 401 as application/problem+json (D11).
@Component
class ProblemAuthenticationEntryPoint implements AuthenticationEntryPoint {

    static final String AUTHENTICATION_REQUIRED = "Authentication required";
    static final String INVALID_TOKEN = "Invalid or expired token";

    private final ObjectMapper objectMapper;

    ProblemAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        boolean tokenWasSent = exception instanceof OAuth2AuthenticationException;
        String detail = tokenWasSent ? INVALID_TOKEN : AUTHENTICATION_REQUIRED;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> problem = Map.of(
                "type", "about:blank",
                "title", status.getReasonPhrase(),
                "status", status.value(),
                "detail", detail
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
