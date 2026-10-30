package com.example.timeoff.service;

// The request does not exist (404, specification section 8).
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
