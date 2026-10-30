package com.example.timeoff.api;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffType;
import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.service.NewRequest;
import com.example.timeoff.service.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class RequestController {

    private final RequestService requestService;
    private final CurrentUser currentUser;

    RequestController(RequestService requestService, CurrentUser currentUser) {
        this.requestService = requestService;
        this.currentUser = currentUser;
    }

    // The owner is always the logged-in user (Q29).
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    RequestView create(@Valid @RequestBody CreateRequestBody body) {
        Long userId = currentUser.id();
        NewRequest command = new NewRequest(
                body.type(),
                body.startDate(),
                body.endDate(),
                body.reason()
        );
        TimeOffRequest created = requestService.create(userId, command);

        return RequestView.from(created);
    }

    // Checks 1 and 2 from interface T4: the shape of the input.
    record CreateRequestBody(
            @NotNull TimeOffType type,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @Size(max = 500) String reason
    ) {
    }
}
