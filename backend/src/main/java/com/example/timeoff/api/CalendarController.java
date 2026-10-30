package com.example.timeoff.api;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.Team;
import com.example.timeoff.domain.TimeOffType;
import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.service.CalendarEntry;
import com.example.timeoff.service.CalendarService;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// The Team calendar screen (interface T7): absences in a month and the teams for the filter (Q43, D12).
@RestController
@RequestMapping("/api")
class CalendarController {

    private final CalendarService calendarService;
    private final CurrentUser currentUser;

    CalendarController(CalendarService calendarService, CurrentUser currentUser) {
        this.calendarService = calendarService;
        this.currentUser = currentUser;
    }

    // Check 1 from interface T7: month is required as yyyy-MM, teamId is optional.
    @GetMapping("/calendar")
    List<CalendarEntryView> calendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(required = false) Long teamId
    ) {
        Long viewerId = currentUser.id();

        return calendarService.month(viewerId, month, teamId)
                .stream()
                .map(CalendarEntryView::from)
                .toList();
    }

    @GetMapping("/teams")
    List<TeamView> teams() {
        Long viewerId = currentUser.id();

        return calendarService.teams(viewerId)
                .stream()
                .map(TeamView::from)
                .toList();
    }

    // With LIMITED access the keys type and status are left out of the JSON (L27, point 7).
    @JsonInclude(JsonInclude.Include.NON_NULL)
    record CalendarEntryView(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            TimeOffType type,
            Status status
    ) {

        static CalendarEntryView from(CalendarEntry entry) {
            return new CalendarEntryView(
                    entry.name(),
                    entry.startDate(),
                    entry.endDate(),
                    entry.type(),
                    entry.status()
            );
        }
    }

    record TeamView(Long id, String name) {

        static TeamView from(Team team) {
            return new TeamView(team.getId(), team.getName());
        }
    }
}
