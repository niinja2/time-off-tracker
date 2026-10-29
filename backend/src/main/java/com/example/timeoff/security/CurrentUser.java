package com.example.timeoff.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Gives controllers the id of the logged-in user; services load the User themselves (L12, point 1).
@Component
public class CurrentUser {

    public Long id() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String subject = authentication.getName();

        return Long.valueOf(subject);
    }
}
