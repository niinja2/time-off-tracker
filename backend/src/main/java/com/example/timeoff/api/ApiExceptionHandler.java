package com.example.timeoff.api;

import com.example.timeoff.domain.InvalidStatusTransitionException;
import com.example.timeoff.security.InvalidLoginException;
import com.example.timeoff.service.InvalidRequestException;
import com.example.timeoff.service.RuleViolationException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.converter.HttpMessageNotReadableException;
import tools.jackson.core.JacksonException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Turns exceptions into the statuses from specification section 8, as application/problem+json (D11).
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    ProblemDetail invalidLogin(InvalidLoginException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    ProblemDetail invalidStatusTransition(InvalidStatusTransitionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    ProblemDetail invalidRequest(InvalidRequestException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RuleViolationException.class)
    ProblemDetail ruleViolation(RuleViolationException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    // The detail names every missing or empty field, e.g. "username must not be blank".
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String detail = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);

        return handleExceptionInternal(
                exception,
                problem,
                headers,
                status,
                request
        );
    }

    // An unreadable body (unknown type, malformed date) names the field, e.g. "type is invalid" (interface T4, check 1).
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String field = unreadableField(exception);
        String detail = field == null ? "Request body is not valid JSON" : field + " is invalid";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);

        return handleExceptionInternal(
                exception,
                problem,
                headers,
                status,
                request
        );
    }

    private String unreadableField(HttpMessageNotReadableException exception) {
        if (!(exception.getCause() instanceof JacksonException jackson)) {
            return null;
        }

        List<JacksonException.Reference> path = jackson.getPath();
        if (path.isEmpty()) {
            return null;
        }

        return path.getFirst()
                .getPropertyName();
    }
}
