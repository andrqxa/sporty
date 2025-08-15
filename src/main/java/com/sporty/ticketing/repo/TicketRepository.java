package com.sporty.ticketing.repo;

import com.sporty.ticketing.model.Ticket;

import java.util.Optional;
import java.util.UUID;

/**
 * Minimal abstraction for ticket persistence operations.
 * <p>
 * Implementations of this interface are responsible for storing and retrieving
 * {@link Ticket} entities. This can be backed by an in-memory store, a relational
 * database, a NoSQL store, or any other persistence mechanism.
 * </p>
 */
public interface TicketRepository {

    /**
     * Persists or updates the given {@link Ticket} entity.
     *
     * @param ticket the {@link Ticket} to save
     * @return the saved {@link Ticket} instance (may be the same or a new instance depending on implementation)
     */
    Ticket save(Ticket ticket);

    /**
     * Retrieves a {@link Ticket} by its unique identifier.
     *
     * @param id the {@link UUID} of the ticket
     * @return an {@link Optional} containing the found ticket, or empty if not found
     */
    Optional<Ticket> findById(UUID id);
}
