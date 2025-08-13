package com.sporty.ticketing.dto;

import com.sporty.ticketing.model.Ticket;
import com.sporty.ticketing.model.TicketStatus;
import java.time.Instant;
import java.util.UUID;

/** API response view for a ticket. */
public record TicketResponse(
    UUID ticketId,
    String subject,
    String description,
    TicketStatus status,
    String userId,
    String assigneeId,
    Instant createdAt,
    Instant updatedAt) {
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
