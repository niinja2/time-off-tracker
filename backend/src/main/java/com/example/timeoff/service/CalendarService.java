package com.example.timeoff.service;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.Team;
import com.example.timeoff.domain.TeamRepository;
import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Who is absent in a month, by the visibility rule (interface T7). Rejected and cancelled requests are never shown (Q48).
@Service
public class CalendarService {

    private static final Comparator<CalendarEntry> BY_START_THEN_NAME = Comparator
            .comparing(CalendarEntry::startDate)
            .thenComparing(CalendarEntry::name);

    private static final Comparator<Team> BY_NAME = Comparator.comparing(Team::getName);

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TimeOffRequestRepository requestRepository;
    private final VisibilityPolicy visibilityPolicy;

    CalendarService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            TimeOffRequestRepository requestRepository,
            VisibilityPolicy visibilityPolicy
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.requestRepository = requestRepository;
        this.visibilityPolicy = visibilityPolicy;
    }

    // teamId may be null (no filter); the filter only narrows the list, visibility stays the same.
    @Transactional(readOnly = true)
    public List<CalendarEntry> month(Long viewerId, YearMonth month, Long teamId) {
        User viewer = loadViewer(viewerId);
        LocalDate first = month.atDay(1);
        LocalDate last = month.atEndOfMonth();

        return requestRepository.findAll()
                .stream()
                .filter(CalendarService::isShown)
                .filter(request -> request.overlaps(first, last))
                .filter(request -> isInTeam(request.getOwner(), teamId))
                .map(request -> toEntry(viewer, request))
                .flatMap(Optional::stream)
                .sorted(BY_START_THEN_NAME)
                .toList();
    }

    // An admin gets every team; others the teams they are members of (a lead is a member of the teams they lead, Q3).
    @Transactional(readOnly = true)
    public List<Team> teams(Long viewerId) {
        User viewer = loadViewer(viewerId);
        List<Team> teams = viewer.isAdmin() ? teamRepository.findAll() : List.copyOf(viewer.getTeams());

        return teams.stream()
                .sorted(BY_NAME)
                .toList();
    }

    private static boolean isShown(TimeOffRequest request) {
        Status status = request.getStatus();

        return status == Status.PENDING || status == Status.APPROVED;
    }

    // FULL: pending and approved, with type and status (Q42, Q43). LIMITED: approved only, name and dates (Q47).
    private Optional<CalendarEntry> toEntry(User viewer, TimeOffRequest request) {
        User owner = request.getOwner();
        Access access = visibilityPolicy.access(viewer, owner);
        boolean approved = request.getStatus() == Status.APPROVED;

        if (access == Access.FULL) {
            return Optional.of(fullEntry(request));
        }
        if (access == Access.LIMITED && approved) {
            return Optional.of(limitedEntry(request));
        }

        return Optional.empty();
    }

    private CalendarEntry fullEntry(TimeOffRequest request) {
        User owner = request.getOwner();

        return new CalendarEntry(
                owner.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getType(),
                request.getStatus()
        );
    }

    private CalendarEntry limitedEntry(TimeOffRequest request) {
        User owner = request.getOwner();

        return new CalendarEntry(
                owner.getName(),
                request.getStartDate(),
                request.getEndDate(),
                null,
                null
        );
    }

    private boolean isInTeam(User owner, Long teamId) {
        if (teamId == null) {
            return true;
        }

        return owner.getTeams()
                .stream()
                .anyMatch(team -> Objects.equals(team.getId(), teamId));
    }

    private User loadViewer(Long viewerId) {
        return userRepository.findById(viewerId)
                .orElseThrow();
    }
}
