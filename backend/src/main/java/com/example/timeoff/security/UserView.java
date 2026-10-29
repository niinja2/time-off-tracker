package com.example.timeoff.security;

import com.example.timeoff.domain.User;

// The <Me> body from the T2 interface; built inside a transaction because teamLead reads lazy teams.
public record UserView(
        Long id,
        String username,
        String name,
        boolean admin,
        boolean teamLead
) {

    static UserView of(User user) {
        return new UserView(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.isAdmin(),
                user.isTeamLead()
        );
    }
}
