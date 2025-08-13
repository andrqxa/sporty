package com.sporty.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for creating a new ticket.
 */
public record CreateTicketRequest(
        @NotBlank String userId,
        @NotBlank String subject,
        String description
) {}
