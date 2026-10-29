package com.example.timeoff.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum Status {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED;

    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = Map.of(
            PENDING, EnumSet.of(APPROVED, REJECTED, CANCELLED),
            APPROVED, EnumSet.of(CANCELLED),
            REJECTED, EnumSet.noneOf(Status.class),
            CANCELLED, EnumSet.noneOf(Status.class)
    );

    public boolean canTransitionTo(Status target) {
        return ALLOWED_TRANSITIONS.get(this).contains(target);
    }
}
