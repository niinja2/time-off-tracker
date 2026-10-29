package com.example.timeoff.service;

// The request is invalid on its own (400, specification section 8).
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
