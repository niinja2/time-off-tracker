package com.example.timeoff.service;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.TimeOffType;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Creates requests. Checks 3-8 from interface T4 run in that order; the first failure wins (L21, point 2).
@Service
public class RequestService {

    private final UserRepository userRepository;
    private final TimeOffRequestRepository requestRepository;
    private final BalanceService balanceService;
    private final WorkingDays workingDays;
    private final Clock clock;

    RequestService(
            UserRepository userRepository,
            TimeOffRequestRepository requestRepository,
            BalanceService balanceService,
            WorkingDays workingDays,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.balanceService = balanceService;
        this.workingDays = workingDays;
        this.clock = clock;
    }

    @Transactional
    public TimeOffRequest create(Long userId, NewRequest command) {
        User owner = userRepository.findById(userId)
                .orElseThrow();
        LocalDate start = command.startDate();
        LocalDate end = command.endDate();

        checkDateOrder(start, end);
        checkSingleYear(start, end);
        checkHasWorkingDay(start, end);
        checkNotBeforeHireDate(owner, start);
        checkNoOverlap(owner, start, end);
        checkBalance(userId, command);

        String reason = Texts.blankToNull(command.reason());
        Instant now = clock.instant();
        TimeOffRequest request = new TimeOffRequest(
                owner,
                command.type(),
                start,
                end,
                reason,
                now
        );

        return requestRepository.save(request);
    }

    // R8
    private void checkDateOrder(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new InvalidRequestException("End date must not be before start date");
        }
    }

    // Specification section 5: a request stays within one calendar year.
    private void checkSingleYear(LocalDate start, LocalDate end) {
        if (start.getYear() != end.getYear()) {
            throw new InvalidRequestException("Request must be within one calendar year");
        }
    }

    // R10
    private void checkHasWorkingDay(LocalDate start, LocalDate end) {
        int days = workingDays.count(start, end);

        if (days == 0) {
            throw new InvalidRequestException("Request must contain at least one working day");
        }
    }

    // D4
    private void checkNotBeforeHireDate(User owner, LocalDate start) {
        if (start.isBefore(owner.getHireDate())) {
            throw new RuleViolationException("Request must not start before hire date");
        }
    }

    // R9: pending or approved requests of any type; touching on an edge is an overlap.
    private void checkNoOverlap(User owner, LocalDate start, LocalDate end) {
        List<TimeOffRequest> existing = requestRepository.findByOwner(owner);
        boolean overlaps = existing.stream()
                .filter(request -> request.getStatus() == Status.PENDING || request.getStatus() == Status.APPROVED)
                .anyMatch(request -> request.overlaps(start, end));

        if (overlaps) {
            throw new RuleViolationException("Request overlaps an existing pending or approved request");
        }
    }

    // R13 for vacation only (R14), against the year the request falls in (D5, L21 point 5).
    private void checkBalance(Long userId, NewRequest command) {
        if (command.type() != TimeOffType.VACATION) {
            return;
        }

        int year = command.startDate().getYear();
        Balance balance = balanceService.balance(userId, year);
        int requested = workingDays.count(command.startDate(), command.endDate());
        int remaining = balance.remaining();

        if (requested > remaining) {
            throw new RuleViolationException(
                    "Not enough vacation days: requested " + requested + ", remaining " + remaining
            );
        }
    }
}
