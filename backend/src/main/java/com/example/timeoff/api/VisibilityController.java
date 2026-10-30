package com.example.timeoff.api;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.service.Balance;
import com.example.timeoff.service.VisibilityService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Requests and balances with details (interface T7). Lists never give 403, only an empty list.
@RestController
@RequestMapping("/api")
class VisibilityController {

    private final VisibilityService visibilityService;
    private final CurrentUser currentUser;

    VisibilityController(VisibilityService visibilityService, CurrentUser currentUser) {
        this.visibilityService = visibilityService;
        this.currentUser = currentUser;
    }

    @GetMapping("/me/requests")
    List<RequestView> myRequests() {
        Long userId = currentUser.id();

        return toViews(visibilityService.mine(userId));
    }

    @GetMapping("/requests")
    List<RequestView> requests() {
        Long viewerId = currentUser.id();

        return toViews(visibilityService.visible(viewerId));
    }

    @GetMapping("/requests/{id}")
    RequestView request(@PathVariable Long id) {
        Long viewerId = currentUser.id();
        TimeOffRequest request = visibilityService.get(id, viewerId);

        return RequestView.from(request);
    }

    @GetMapping("/users/{id}/balance")
    Balance balance(@PathVariable Long id) {
        Long viewerId = currentUser.id();

        return visibilityService.balance(id, viewerId);
    }

    private List<RequestView> toViews(List<TimeOffRequest> requests) {
        return requests.stream()
                .map(RequestView::from)
                .toList();
    }
}
