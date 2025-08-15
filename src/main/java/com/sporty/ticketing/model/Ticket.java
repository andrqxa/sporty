package com.sporty.ticketing.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing a support ticket in the system.
 * <p>
 * A {@code Ticket} captures the essential information for a customer
 * support case, including:
 * <ul>
 *   <li>Unique ticket identifier</li>
 *   <li>Subject and description of the issue</li>
 *   <li>Ticket status ({@link TicketStatus})</li>
 *   <li>User who created the ticket</li>
 *   <li>Agent assigned to handle the ticket (optional)</li>
 *   <li>Timestamps for creation and last update</li>
 * </ul>
 * <p>
 * Instances are typically created via the static factory
 * {@link #newTicket(String, String, String)}, which sets default
 * values and timestamps.
 */
public class Ticket {

    /** Unique identifier of the ticket. */
    private UUID ticketId;

    /** Short summary of the issue. */
    private String subject;

    /** Detailed description of the issue. */
    private String description;

    /** Current status of the ticket. */
    private TicketStatus status;

    /** ID of the user who created the ticket. */
    private String userId;

    /** ID of the agent assigned to the ticket (nullable). */
    private String assigneeId;

    /** When the ticket was created. */
    private Instant createdAt;

    /** When the ticket was last updated. */
    private Instant updatedAt;

    /**
     * Creates a new {@code Ticket} instance with default status {@link TicketStatus#OPEN}.
     * <p>
     * This method:
     * <ul>
     *   <li>Generates a random UUID</li>
     *   <li>Sets the creation and update timestamps to {@link Instant#now()}</li>
     *   <li>Defaults description to an empty string if {@code null}</li>
     * </ul>
     *
     * @param userId      ID of the user creating the ticket (required)
     * @param subject     short summary of the issue (required)
     * @param description detailed description of the issue (nullable, defaults to empty string)
     * @return a fully initialized {@code Ticket} instance
     * @throws NullPointerException if {@code userId} or {@code subject} is {@code null}
     */
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

    /**
     * Assigns the ticket to an agent and updates the {@code updatedAt} timestamp.
     *
     * @param assigneeId the ID of the assigned agent
     * @throws NullPointerException if {@code assigneeId} is {@code null}
     */
    public void assign(String assigneeId) {
        this.assigneeId = Objects.requireNonNull(assigneeId, "assigneeId");
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the ticket's status and refreshes the {@code updatedAt} timestamp.
     *
     * @param status the new ticket status
     * @throws NullPointerException if {@code status} is {@code null}
     */
    public void updateStatus(TicketStatus status) {
        this.status = Objects.requireNonNull(status, "status");
        this.updatedAt = Instant.now();
    }

    // Getters

    public UUID getTicketId() { return ticketId; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public String getUserId() { return userId; }
    public String getAssigneeId() { return assigneeId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Setters (also update the updatedAt timestamp)

    public void setSubject(String subject) {
        this.subject = subject;
        this.updatedAt = Instant.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }
}
