package com.sporty.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for creating a new ticket.
 * <p>
 * This DTO is used in {@code POST /tickets} requests to specify
 * the details of the ticket being created.
 * <p>
 * Validation:
 * <ul>
 *   <li>{@code userId} — must not be {@code null}, empty, or contain only whitespace.</li>
 *   <li>{@code subject} — must not be {@code null}, empty, or contain only whitespace.</li>
 *   <li>{@code description} — optional field, may be {@code null} or empty.</li>
 * </ul>
 *
 * @param userId     unique identifier of the user creating the ticket; must not be blank
 * @param subject    short description or title of the ticket; must not be blank
 * @param description optional detailed description of the issue
 */
public record CreateTicketRequest(
        @NotBlank String userId,
        @NotBlank String subject,
        String description
) {}
