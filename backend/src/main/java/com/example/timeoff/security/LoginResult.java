package com.example.timeoff.security;

public record LoginResult(String token, UserView user) {
}
