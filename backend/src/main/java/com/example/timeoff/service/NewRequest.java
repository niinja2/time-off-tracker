package com.example.timeoff.service;

import com.example.timeoff.domain.TimeOffType;
import java.time.LocalDate;

// Input for creating a request; the owner is always the logged-in user (Q29).
public record NewRequest(
        TimeOffType type,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) {
}
