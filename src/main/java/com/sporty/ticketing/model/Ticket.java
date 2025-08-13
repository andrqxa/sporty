package com.sporty.ticketing.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model for a support ticket.
 */
public class Ticket {

    private UUID ticketId;
    private String subject;
    private String description;
    private TicketStatus status;
    private String userId;
    private String assigneeId; // nullable
    private Instant createdAt;
    private Instant updatedAt;

    /** Factory method for a new ticket. */
    public static Ticket newTicket(String userId, String subject, String description) {
        Ticket t = new Ticket();
        t.ticketId = UUID.randomUUID();
        t.userId = Objects.requireNonNull(userId, "userId");
        t.subject = Objects.requireNonNull(subject, "subject");
        t.description = Objects.requireNonNullElse(description, "");
        t.status = TicketStatus.OPEN;
        t.createdAt = Instant.now();
        t.updatedAt = t.createdAt;
        return t;
    }

    /** Assigns the ticket to an agent. */
    public void assign(String assigneeId) {
        this.assigneeId = Objects.requireNonNull(assigneeId, "assigneeId");
        this.updatedAt = Instant.now();
    }

    /** Updates the ticket status. */
    public void updateStatus(TicketStatus status) {
        this.status = Objects.requireNonNull(status, "status");
        this.updatedAt = Instant.now();
    }

    // Getters & setters

    public UUID getTicketId() { return ticketId; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public String getUserId() { return userId; }
    public String getAssigneeId() { return assigneeId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setSubject(String subject) { this.subject = subject; this.updatedAt = Instant.now(); }
    public void setDescription(String description) { this.description = description; this.updatedAt = Instant.now(); }
}
