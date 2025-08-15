package com.sporty.ticketing.dto;

import com.sporty.ticketing.model.Ticket;
import com.sporty.ticketing.model.TicketStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * API response representation of a ticket.
 * <p>
 * This record is returned by the Ticket API endpoints
 * to expose ticket details to clients.
 *
 * @param ticketId   unique identifier of the ticket
 * @param subject    short description or title of the ticket
 * @param description optional detailed description of the ticket
 * @param status     current status of the ticket
 * @param userId     identifier of the user who created the ticket
 * @param assigneeId identifier of the agent assigned to the ticket (may be {@code null})
 * @param createdAt  timestamp when the ticket was created
 * @param updatedAt  timestamp when the ticket was last updated
 */
public record TicketResponse(
        UUID ticketId,
        String subject,
        String description,
        TicketStatus status,
        String userId,
        String assigneeId,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Creates a {@link TicketResponse} from a domain {@link Ticket} entity.
     *
     * @param t the {@link Ticket} to convert, must not be {@code null}
     * @return a new {@link TicketResponse} containing the data from the given ticket
     */
    public static TicketResponse from(Ticket t) {
        return new TicketResponse(
                t.getTicketId(),
                t.getSubject(),
                t.getDescription(),
                t.getStatus(),
                t.getUserId(),
                t.getAssigneeId(),
                t.getCreatedAt(),
                t.getUpdatedAt());
    }
}
