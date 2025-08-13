package com.sporty.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for assigning a ticket to an agent.
 */
public record AssignRequest(
        @NotBlank String assigneeId
) {}
