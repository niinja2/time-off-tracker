package com.example.timeoff.service;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Approves and rejects requests. Checks 3-5 from interface T5 run in that order: exists, may decide, is pending.
@Service
public class ApprovalService {

    private static final Comparator<TimeOffRequest> BY_START_THEN_ID = Comparator
            .comparing(TimeOffRequest::getStartDate)
            .thenComparing(TimeOffRequest::getId);

    private final UserRepository userRepository;
    private final TimeOffRequestRepository requestRepository;
    private final ApprovalPolicy approvalPolicy;
    private final Clock clock;

    ApprovalService(
            UserRepository userRepository,
            TimeOffRequestRepository requestRepository,
            ApprovalPolicy approvalPolicy,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.approvalPolicy = approvalPolicy;
        this.clock = clock;
    }

    @Transactional
    public TimeOffRequest approve(Long requestId, Long actorId, String comment) {
        User actor = loadActor(actorId);
        TimeOffRequest request = loadDecidable(requestId, actor);
        String decisionComment = Texts.blankToNull(comment);
        Instant now = clock.instant();

        request.approve(actor, decisionComment, now);

        return request;
    }

    // The comment is required (R5); the API has already rejected a blank one.
    @Transactional
    public TimeOffRequest reject(Long requestId, Long actorId, String comment) {
        User actor = loadActor(actorId);
        TimeOffRequest request = loadDecidable(requestId, actor);
        Instant now = clock.instant();

        request.reject(actor, comment, now);

        return request;
    }

    // Pending requests the actor may decide, for the Approvals screen.
    @Transactional(readOnly = true)
    public List<TimeOffRequest> pending(Long actorId) {
        User actor = loadActor(actorId);

        return requestRepository.findByStatus(Status.PENDING)
                .stream()
                .filter(request -> approvalPolicy.canDecide(actor, request))
                .sorted(BY_START_THEN_ID)
                .toList();
    }

    private User loadActor(Long actorId) {
        return userRepository.findById(actorId)
                .orElseThrow();
    }

    // The pending state (R4) is checked by the request itself when it changes (T1).
    private TimeOffRequest loadDecidable(Long requestId, User actor) {
        TimeOffRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!approvalPolicy.canDecide(actor, request)) {
            throw new ForbiddenException("You cannot approve or reject this request");
        }

        return request;
    }
}
