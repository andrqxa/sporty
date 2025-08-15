package com.sporty.ticketing.model;

/**
 * Enumeration of possible lifecycle states for a support ticket.
 * <p>
 * These states define the ticket's progress from creation to closure:
 * <ul>
 *   <li>{@link #OPEN} — newly created ticket, awaiting processing or assignment</li>
 *   <li>{@link #IN_PROGRESS} — ticket currently being worked on by an agent</li>
 *   <li>{@link #RESOLVED} — ticket resolved; solution provided but not yet confirmed closed</li>
 *   <li>{@link #CLOSED} — ticket fully closed; no further action required</li>
 * </ul>
 * <p>
 * State transitions are controlled by business logic in the service layer.
 */
public enum TicketStatus {
    /** Newly created ticket, not yet assigned or started. */
    OPEN,

    /** Ticket actively being handled by an assigned agent. */
    IN_PROGRESS,

    /** Ticket resolved; awaiting final confirmation or closure. */
    RESOLVED,

    /** Ticket closed; no additional work or changes expected. */
    CLOSED
}
