package com.sporty.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for assigning a ticket to a specific agent.
 * <p>
 * This DTO is used in {@code PATCH /tickets/{ticketId}/assign} requests
 * to specify the identifier of the agent to whom the ticket should be assigned.
 * <p>
 * Validation:
 * <ul>
 *   <li>{@code assigneeId} must not be {@code null}, empty, or contain only whitespace.</li>
 * </ul>
 *
 * @param assigneeId unique identifier of the agent to assign the ticket to; must not be blank
 */
public record AssignRequest(
        @NotBlank String assigneeId
) {}
