package com.sporty.ticketing.dto;

import com.sporty.ticketing.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for updating ticket status.
 */
public record UpdateStatusRequest(
        @NotNull TicketStatus status
) {}
