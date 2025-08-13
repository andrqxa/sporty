package com.sporty.ticketing.repo;

import com.sporty.ticketing.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository backed by ConcurrentHashMap.
 */
@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private final ConcurrentHashMap<UUID, Ticket> store = new ConcurrentHashMap<>();

    @Override
    public Ticket save(Ticket ticket) {
        store.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
