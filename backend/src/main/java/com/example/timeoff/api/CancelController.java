package com.example.timeoff.api;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.service.CancelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class CancelController {

    private final CancelService cancelService;
    private final CurrentUser currentUser;

    CancelController(CancelService cancelService, CurrentUser currentUser) {
        this.cancelService = cancelService;
        this.currentUser = currentUser;
    }

    // The body is optional; whether a reason is needed depends on who cancels (D3), so the service checks it.
    @PostMapping("/requests/{id}/cancel")
    RequestView cancel(@PathVariable Long id, @Valid @RequestBody(required = false) CancelBody body) {
        Long actorId = currentUser.id();
        String reason = body == null ? null : body.reason();
        TimeOffRequest cancelled = cancelService.cancel(id, actorId, reason);

        return RequestView.from(cancelled);
    }

    // Check 1 from interface T6: the shape of the input.
    record CancelBody(@Size(max = 500) String reason) {
    }
}
