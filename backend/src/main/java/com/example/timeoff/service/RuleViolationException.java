package com.example.timeoff.service;

// The request conflicts with stored data: hire date, overlap or balance (409, specification section 8).
public class RuleViolationException extends RuntimeException {

    public RuleViolationException(String message) {
        super(message);
    }
}
