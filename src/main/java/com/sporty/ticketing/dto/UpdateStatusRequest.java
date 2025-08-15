package com.sporty.ticketing.dto;

import com.sporty.ticketing.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for updating the status of an existing ticket.
 * <p>
 * This DTO is used in API endpoints to validate and transfer
 * the new ticket status sent by the client.
 *
 * @param status the new {@link TicketStatus} to be set for the ticket,
 *               must not be {@code null}
 */
public record UpdateStatusRequest(
        @NotNull TicketStatus status
) {}
