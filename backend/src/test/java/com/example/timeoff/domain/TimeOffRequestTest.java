package com.example.timeoff.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimeOffRequestTest {

    private static final Instant CREATED = Instant.parse("2026-10-01T08:00:00Z");
    private static final Instant DECIDED = Instant.parse("2026-10-02T09:00:00Z");

    private User ivan;
    private User ana;

    @BeforeEach
    void setUp() {
        ivan = new User(
            "ivan",
            "Ivan",
            "hash",
            false,
            LocalDate.of(2020, 1, 1));
        ana = new User(
            "ana",
            "Ana",
            "hash",
            false,
            LocalDate.of(2020, 1, 1));
    }

    private TimeOffRequest pendingRequest() {
        return new TimeOffRequest(
            ivan,
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6),
            "Family",
            CREATED);
    }

    @Test
    void newRequestIsPending_T1() {
        TimeOffRequest request = pendingRequest();

        assertThat(request.getStatus()).isEqualTo(Status.PENDING);
        assertThat(request.isPending()).isTrue();
    }

    @Test
    void onlyOwnerIsSubmitter_Q29() {
        TimeOffRequest request = pendingRequest();

        assertThat(request.isSubmittedBy(ivan)).isTrue();
        assertThat(request.isSubmittedBy(ana)).isFalse();
    }

    @Test
    void unsavedCopyOfOwnerIsNotSubmitter_T1() {
        User copyOfIvan = new User(
            "ivan",
            "Ivan",
            "hash",
            false,
            LocalDate.of(2020, 1, 1));

        assertThat(pendingRequest().isSubmittedBy(copyOfIvan)).isFalse();
    }

    @Test
    void approvePendingSetsDecisionFields_R4() {
        TimeOffRequest request = pendingRequest();

        request.approve(ana, "Enjoy", DECIDED);

        assertThat(request.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(request.getDecisionComment()).isEqualTo("Enjoy");
        assertThat(request.getDecidedBy()).isSameAs(ana);
        assertThat(request.getDecidedAt()).isEqualTo(DECIDED);
    }

    @Test
    void rejectPendingSetsDecisionFields_T1() {
        TimeOffRequest request = pendingRequest();

        request.reject(ana, "Team is short", DECIDED);

        assertThat(request.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(request.getDecisionComment()).isEqualTo("Team is short");
        assertThat(request.getDecidedBy()).isSameAs(ana);
        assertThat(request.getDecidedAt()).isEqualTo(DECIDED);
    }

    @Test
    void cancelPendingByOwnerWithoutReason_R6() {
        TimeOffRequest request = pendingRequest();

        request.cancel(ivan, null, DECIDED);

        assertThat(request.getStatus()).isEqualTo(Status.CANCELLED);
        assertThat(request.getCancelReason()).isNull();
        assertThat(request.getCancelledBy()).isSameAs(ivan);
        assertThat(request.getCancelledAt()).isEqualTo(DECIDED);
    }

    @Test
    void cancelApprovedRequest_T1() {
        TimeOffRequest request = pendingRequest();
        request.approve(ana, null, DECIDED);

        request.cancel(ana, "Project moved", DECIDED);

        assertThat(request.getStatus()).isEqualTo(Status.CANCELLED);
        assertThat(request.getCancelReason()).isEqualTo("Project moved");
        assertThat(request.getCancelledBy()).isSameAs(ana);
    }

    @Test
    void approvedRequestCannotBeApprovedAgain_R4() {
        TimeOffRequest request = pendingRequest();
        request.approve(ana, null, DECIDED);

        assertThatThrownBy(() -> request.approve(ana, null, DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void rejectedRequestCannotChangeAgain_R4() {
        TimeOffRequest request = pendingRequest();
        request.reject(ana, "No", DECIDED);

        assertThatThrownBy(() -> request.approve(ana, null, DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
        assertThatThrownBy(() -> request.reject(ana, "No", DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
        assertThatThrownBy(() -> request.cancel(ivan, null, DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void cancelledRequestCannotChangeAgain_R4() {
        TimeOffRequest request = pendingRequest();
        request.cancel(ivan, null, DECIDED);

        assertThatThrownBy(() -> request.approve(ana, null, DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
        assertThatThrownBy(() -> request.reject(ana, "No", DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
        assertThatThrownBy(() -> request.cancel(ivan, null, DECIDED))
            .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @ParameterizedTest(name = "request 2.-6. Nov, check {0}..{1}: overlaps={2}")
    @CsvSource({
        "2026-10-28, 2026-11-01, false",
        "2026-10-28, 2026-11-02, true",
        "2026-11-03, 2026-11-04, true",
        "2026-11-06, 2026-11-10, true",
        "2026-11-07, 2026-11-10, false",
        "2026-10-01, 2026-12-01, true"
    })
    void overlapsIncludesBothEndDates_R9(LocalDate start, LocalDate end, boolean expected) {
        assertThat(pendingRequest().overlaps(start, end)).isEqualTo(expected);
    }
}
