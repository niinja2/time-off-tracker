package com.example.timeoff.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StatusTest {

    @ParameterizedTest(name = "{0} -> {1} allowed={2}")
    @CsvSource({
        "PENDING, APPROVED, true",
        "PENDING, REJECTED, true",
        "PENDING, CANCELLED, true",
        "PENDING, PENDING, false",
        "APPROVED, CANCELLED, true",
        "APPROVED, PENDING, false",
        "APPROVED, APPROVED, false",
        "APPROVED, REJECTED, false",
        "REJECTED, PENDING, false",
        "REJECTED, APPROVED, false",
        "REJECTED, REJECTED, false",
        "REJECTED, CANCELLED, false",
        "CANCELLED, PENDING, false",
        "CANCELLED, APPROVED, false",
        "CANCELLED, REJECTED, false",
        "CANCELLED, CANCELLED, false"
    })
    void statusTransitionFollowsTable_T1(Status from, Status to, boolean allowed) {
        assertThat(from.canTransitionTo(to)).isEqualTo(allowed);
    }
}
