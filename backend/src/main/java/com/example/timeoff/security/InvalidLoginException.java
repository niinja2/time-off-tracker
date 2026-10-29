package com.example.timeoff.security;

public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("Invalid username or password");
    }
}
