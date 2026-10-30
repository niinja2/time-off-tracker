package com.example.timeoff.api;

import com.example.timeoff.domain.Status;
import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffType;
import com.example.timeoff.domain.User;
import java.time.Instant;
import java.time.LocalDate;

// The <Request> body from interfaces T4 to T7 (names of decider and canceller: L32).
record RequestView(
        Long id,
        Long ownerId,
        String ownerName,
        TimeOffType type,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        Status status,
        Instant createdAt,
        String decisionComment,
        Long decidedById,
        String decidedByName,
        Instant decidedAt,
        String cancelReason,
        Long cancelledById,
        String cancelledByName,
        Instant cancelledAt
) {

    static RequestView from(TimeOffRequest request) {
        User owner = request.getOwner();
        Long decidedById = idOrNull(request.getDecidedBy());
        Long cancelledById = idOrNull(request.getCancelledBy());
        String decidedByName = nameOrNull(request.getDecidedBy());
        String cancelledByName = nameOrNull(request.getCancelledBy());

        return new RequestView(
                request.getId(),
                owner.getId(),
                owner.getName(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getReason(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getDecisionComment(),
                decidedById,
                decidedByName,
                request.getDecidedAt(),
                request.getCancelReason(),
                cancelledById,
                cancelledByName,
                request.getCancelledAt()
        );
    }

    private static Long idOrNull(User user) {
        if (user == null) {
            return null;
        }

        return user.getId();
    }

    private static String nameOrNull(User user) {
        if (user == null) {
            return null;
        }

        return user.getName();
    }
}
