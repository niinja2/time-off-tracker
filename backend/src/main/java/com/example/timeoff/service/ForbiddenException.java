package com.example.timeoff.service;

// The user has no right to this action (403, specification section 8).
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
