package com.example.timeoff.service;

public record Balance(
        int year,
        int entitlement,
        int used,
        int pending,
        int remaining
) {
}
