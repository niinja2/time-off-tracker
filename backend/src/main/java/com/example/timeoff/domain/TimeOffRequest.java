package com.example.timeoff.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "time_off_requests")
public class TimeOffRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    private TimeOffType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String decisionComment;

    @ManyToOne
    @JoinColumn(name = "decided_by_id")
    private User decidedBy;

    private Instant decidedAt;

    private String cancelReason;

    @ManyToOne
    @JoinColumn(name = "cancelled_by_id")
    private User cancelledBy;

    private Instant cancelledAt;

    private Instant createdAt;

    protected TimeOffRequest() {
    }

    public TimeOffRequest(
            User owner,
            TimeOffType type,
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            Instant createdAt
    ) {
        this.owner = owner;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.createdAt = createdAt;
        this.status = Status.PENDING;
    }

    public boolean isSubmittedBy(User user) {
        return owner.isSameAs(user);
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean overlaps(LocalDate start, LocalDate end) {
        boolean endsBeforeThis = end.isBefore(startDate);
        boolean startsAfterThis = start.isAfter(endDate);

        return !endsBeforeThis && !startsAfterThis;
    }

    public void approve(User actor, String comment, Instant now) {
        transitionTo(Status.APPROVED);
        recordDecision(actor, comment, now);
    }

    public void reject(User actor, String comment, Instant now) {
        transitionTo(Status.REJECTED);
        recordDecision(actor, comment, now);
    }

    public void cancel(User actor, String reason, Instant now) {
        transitionTo(Status.CANCELLED);
        this.cancelReason = reason;
        this.cancelledBy = actor;
        this.cancelledAt = now;
    }

    private void transitionTo(Status target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(status, target);
        }

        status = target;
    }

    private void recordDecision(User actor, String comment, Instant now) {
        this.decisionComment = comment;
        this.decidedBy = actor;
        this.decidedAt = now;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public TimeOffType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public Status getStatus() {
        return status;
    }

    public String getDecisionComment() {
        return decisionComment;
    }

    public User getDecidedBy() {
        return decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public User getCancelledBy() {
        return cancelledBy;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
