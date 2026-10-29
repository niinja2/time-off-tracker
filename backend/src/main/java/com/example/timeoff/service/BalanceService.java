package com.example.timeoff.service;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.TimeOffType;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private static final int ANNUAL_DAYS = 20;
    private static final int MONTHS_IN_YEAR = 12;

    private final UserRepository userRepository;
    private final TimeOffRequestRepository requestRepository;
    private final WorkingDays workingDays;

    BalanceService(
            UserRepository userRepository,
            TimeOffRequestRepository requestRepository,
            WorkingDays workingDays
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.workingDays = workingDays;
    }

    // 20 days a year (R11); in the hire year 20 x months from the hire month to December / 12, rounded up (Q15, D5).
    public int entitlement(LocalDate hireDate, int year) {
        int hireYear = hireDate.getYear();

        if (year < hireYear) {
            return 0;
        }
        if (year > hireYear) {
            return ANNUAL_DAYS;
        }

        int monthsEmployed = MONTHS_IN_YEAR - hireDate.getMonthValue() + 1;

        return Math.ceilDiv(ANNUAL_DAYS * monthsEmployed, MONTHS_IN_YEAR);
    }

    // Pending requests use up the balance too (R13); sick leave and days off do not (R14).
    @Transactional(readOnly = true)
    public Balance balance(Long userId, int year) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        List<TimeOffRequest> requests = requestRepository.findByOwner(user);
        int entitlement = entitlement(user.getHireDate(), year);
        int used = vacationDays(requests, year, Status.APPROVED);
        int pending = vacationDays(requests, year, Status.PENDING);
        int remaining = entitlement - used - pending;

        return new Balance(
                year,
                entitlement,
                used,
                pending,
                remaining
        );
    }

    // A request belongs to the year it starts in; it never crosses a year boundary (specification, section 5).
    private int vacationDays(List<TimeOffRequest> requests, int year, Status status) {
        return requests.stream()
                .filter(request -> request.getType() == TimeOffType.VACATION)
                .filter(request -> request.getStatus() == status)
                .filter(request -> request.getStartDate().getYear() == year)
                .mapToInt(request -> workingDays.count(request.getStartDate(), request.getEndDate()))
                .sum();
    }
}
