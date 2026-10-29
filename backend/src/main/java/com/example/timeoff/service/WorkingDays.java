package com.example.timeoff.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.stereotype.Component;

// The one place that decides what a working day is; holidays would be added here (D1).
@Component
public class WorkingDays {

    private static final Set<DayOfWeek> WEEKEND = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    public boolean isWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return !WEEKEND.contains(dayOfWeek);
    }

    // Both dates inclusive (R10). Date order is checked when a request is created (R8, T4).
    public int count(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            return 0;
        }

        LocalDate dayAfterEnd = end.plusDays(1);
        long workingDays = start.datesUntil(dayAfterEnd)
                .filter(this::isWorkingDay)
                .count();

        return (int) workingDays;
    }
}
