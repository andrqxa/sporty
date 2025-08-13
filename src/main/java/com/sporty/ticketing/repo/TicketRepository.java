package com.sporty.ticketing.repo;

import com.sporty.ticketing.model.Ticket;

import java.util.Optional;
import java.util.UUID;

/**
 * Minimal repository abstraction.
 */
public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(UUID id);
}
