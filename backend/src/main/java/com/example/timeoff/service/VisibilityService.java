package com.example.timeoff.service;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Requests and balances with details, by the visibility rule (interface T7). Checks 2-3 run in order: exists, may view.
@Service
public class VisibilityService {

    private static final Comparator<TimeOffRequest> NEWEST_FIRST = Comparator
            .comparing(TimeOffRequest::getStartDate)
            .thenComparing(TimeOffRequest::getId)
            .reversed();

    private final UserRepository userRepository;
    private final TimeOffRequestRepository requestRepository;
    private final VisibilityPolicy visibilityPolicy;
    private final BalanceService balanceService;
    private final Clock clock;

    VisibilityService(
            UserRepository userRepository,
            TimeOffRequestRepository requestRepository,
            VisibilityPolicy visibilityPolicy,
            BalanceService balanceService,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.visibilityPolicy = visibilityPolicy;
        this.balanceService = balanceService;
        this.clock = clock;
    }

    // Own requests in every state, with all details (Q44-Q46).
    @Transactional(readOnly = true)
    public List<TimeOffRequest> mine(Long userId) {
        User user = loadViewer(userId);

        return requestRepository.findByOwner(user)
                .stream()
                .sorted(NEWEST_FIRST)
                .toList();
    }

    // Every request whose owner is FULL for the viewer; an employee gets only their own.
    @Transactional(readOnly = true)
    public List<TimeOffRequest> visible(Long viewerId) {
        User viewer = loadViewer(viewerId);

        return requestRepository.findAll()
                .stream()
                .filter(request -> seesAll(viewer, request.getOwner()))
                .sorted(NEWEST_FIRST)
                .toList();
    }

    @Transactional(readOnly = true)
    public TimeOffRequest get(Long requestId, Long viewerId) {
        User viewer = loadViewer(viewerId);
        TimeOffRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!seesAll(viewer, request.getOwner())) {
            throw new ForbiddenException("You cannot view this request");
        }

        return request;
    }

    // Balance of a user for the current year (Q16, Q17); the date is taken in the clock's zone (D8, L18).
    @Transactional(readOnly = true)
    public Balance balance(Long userId, Long viewerId) {
        User viewer = loadViewer(viewerId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!seesAll(viewer, owner)) {
            throw new ForbiddenException("You cannot view this balance");
        }

        LocalDate today = LocalDate.now(clock);

        return balanceService.balance(userId, today.getYear());
    }

    private boolean seesAll(User viewer, User owner) {
        return visibilityPolicy.access(viewer, owner) == Access.FULL;
    }

    private User loadViewer(Long viewerId) {
        return userRepository.findById(viewerId)
                .orElseThrow();
    }
}
