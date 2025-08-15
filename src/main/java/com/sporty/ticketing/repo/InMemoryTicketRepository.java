package com.sporty.ticketing.repo;

import com.sporty.ticketing.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory implementation of {@link TicketRepository} backed by a {@link ConcurrentHashMap}.
 * <p>
 * This repository is primarily intended for development, testing, and demonstration purposes.
 * It stores {@link Ticket} entities in memory and does not provide persistence across application restarts.
 * </p>
 * <p>
 * Thread-safe due to the use of {@link ConcurrentHashMap}, but should not be used in production
 * where persistent storage is required.
 * </p>
 */
@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private final ConcurrentHashMap<UUID, Ticket> store = new ConcurrentHashMap<>();

    /**
     * Saves or updates a ticket in the in-memory store.
     *
     * @param ticket the {@link Ticket} to save
     * @return the same {@link Ticket} instance for method chaining
     */
    @Override
    public Ticket save(Ticket ticket) {
        store.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    /**
     * Finds a ticket by its ID.
     *
     * @param id unique {@link UUID} of the ticket
     * @return an {@link Optional} containing the ticket if found, or empty if not present
     */
    @Override
    public Optional<Ticket> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
