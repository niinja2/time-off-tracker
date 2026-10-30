package com.example.timeoff.api;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.service.ApprovalService;
import com.example.timeoff.service.InvalidRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class ApprovalController {

    private final ApprovalService approvalService;
    private final CurrentUser currentUser;

    ApprovalController(ApprovalService approvalService, CurrentUser currentUser) {
        this.approvalService = approvalService;
        this.currentUser = currentUser;
    }

    // The body and the comment are optional when approving (R5).
    @PostMapping("/requests/{id}/approve")
    RequestView approve(@PathVariable Long id, @Valid @RequestBody(required = false) ApproveBody body) {
        Long actorId = currentUser.id();
        String comment = body == null ? null : body.comment();
        TimeOffRequest approved = approvalService.approve(id, actorId, comment);

        return RequestView.from(approved);
    }

    // A rejection needs a comment (R5); a missing body counts as a missing comment.
    @PostMapping("/requests/{id}/reject")
    RequestView reject(@PathVariable Long id, @Valid @RequestBody(required = false) RejectBody body) {
        if (body == null) {
            throw new InvalidRequestException("comment must not be blank");
        }

        Long actorId = currentUser.id();
        TimeOffRequest rejected = approvalService.reject(id, actorId, body.comment());

        return RequestView.from(rejected);
    }

    @GetMapping("/approvals")
    List<RequestView> approvals() {
        Long actorId = currentUser.id();

        return approvalService.pending(actorId)
                .stream()
                .map(RequestView::from)
                .toList();
    }

    // Check 1 from interface T5: the shape of the input.
    record ApproveBody(@Size(max = 500) String comment) {
    }

    // Checks 1 and 2 from interface T5.
    record RejectBody(@NotBlank @Size(max = 500) String comment) {
    }
}
