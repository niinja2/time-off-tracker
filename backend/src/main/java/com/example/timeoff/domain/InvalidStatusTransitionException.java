package com.example.timeoff.domain;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(Status from, Status to) {
        super("Request cannot change from " + from + " to " + to);
    }
}
