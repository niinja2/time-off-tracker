package com.example.timeoff.service;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.TimeOffType;
import java.time.LocalDate;

// One absence on the calendar (interface T7). With LIMITED access type and status are null.
public record CalendarEntry(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        TimeOffType type,
        Status status
) {
}
