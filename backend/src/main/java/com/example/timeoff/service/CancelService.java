package com.example.timeoff.service;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Cancels requests. Checks 2-6 from interface T6 run in that order; the first failure wins.
@Service
public class CancelService {

    private static final Comparator<TimeOffRequest> BY_START_THEN_ID = Comparator
            .comparing(TimeOffRequest::getStartDate)
            .thenComparing(TimeOffRequest::getId);

    private final UserRepository userRepository;
    private final TimeOffRequestRepository requestRepository;
    private final CancelPolicy cancelPolicy;
    private final Clock clock;

    CancelService(
            UserRepository userRepository,
            TimeOffRequestRepository requestRepository,
            CancelPolicy cancelPolicy,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.cancelPolicy = cancelPolicy;
        this.clock = clock;
    }

    @Transactional
    public TimeOffRequest cancel(Long requestId, Long actorId, String reason) {
        User actor = userRepository.findById(actorId)
                .orElseThrow();
        TimeOffRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        String cancelReason = Texts.blankToNull(reason);

        checkMayCancel(actor, request);
        checkReason(actor, request, cancelReason);
        checkNotStarted(request);

        Instant now = clock.instant();
        request.cancel(actor, cancelReason, now);

        return request;
    }

    // Approved requests of others the actor may cancel and that have not started (interface T8.1, L39).
    @Transactional(readOnly = true)
    public List<TimeOffRequest> cancellable(Long actorId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow();

        return requestRepository.findByStatus(Status.APPROVED)
                .stream()
                .filter(request -> !request.isSubmittedBy(actor))
                .filter(request -> cancelPolicy.canCancel(actor, request))
                .filter(request -> !hasStarted(request))
                .sorted(BY_START_THEN_ID)
                .toList();
    }

    // D14
    private void checkMayCancel(User actor, TimeOffRequest request) {
        if (!cancelPolicy.canCancel(actor, request)) {
            throw new ForbiddenException("You cannot cancel this request");
        }
    }

    // D3
    private void checkReason(User actor, TimeOffRequest request, String reason) {
        boolean missing = reason == null && cancelPolicy.reasonRequired(actor, request);

        if (missing) {
            throw new InvalidRequestException("reason must not be blank");
        }
    }

    // The pending state (R6) has no such limit; terminal states are refused by the request itself (T1).
    private void checkNotStarted(TimeOffRequest request) {
        boolean started = request.getStatus() == Status.APPROVED && hasStarted(request);

        if (started) {
            throw new RuleViolationException("Request has already started");
        }
    }

    // R7, Q23: a request that starts today has started.
    private boolean hasStarted(TimeOffRequest request) {
        LocalDate today = LocalDate.now(clock);

        return !request.getStartDate().isAfter(today);
    }
}
