package com.sporty.ticketing.service;

import com.sporty.ticketing.config.LockProperties;
import com.sporty.ticketing.exception.ConflictException;
import com.sporty.ticketing.exception.NotFoundException;
import com.sporty.ticketing.lock.LockManager;
import com.sporty.ticketing.model.Ticket;
import com.sporty.ticketing.model.TicketStatus;
import com.sporty.ticketing.repo.TicketRepository;
import org.slf4j.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Ticket application service. Critical updates are protected by a distributed lock.
 */
@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository repo;
    private final LockManager locks;
    private final LockProperties props;

    public TicketService(TicketRepository repo, LockManager locks, LockProperties props) {
        this.repo = repo;
        this.locks = locks;
        this.props = props;
    }

    /** Creates and persists a new ticket. */
    public Ticket create(String userId, String subject, String description) {
        var t = Ticket.newTicket(userId, subject, description);
        return repo.save(t);
    }

    /** Assigns a ticket to an agent under a distributed lock. */
    public Ticket assign(UUID id, String assigneeId) {
        var key = "lock:ticket:" + id;
        var ttl = Duration.ofMillis(props.getTtlMs());
        var tokenOpt = locks.tryLockWithRetry(key, ttl, Duration.ofMillis(300)); // small deadline
        if (tokenOpt.isEmpty()) {
            throw new ConflictException("Ticket is locked by another process");
        }
        var token = tokenOpt.get();
        try {
            var t = repo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found: " + id));
            t.assign(assigneeId);
            return repo.save(t);
        } finally {
            boolean released = locks.unlock(key, token);
            if (!released) {
                // Not critical, but useful for diagnostics
                log.debug("Lock was not released (key={}, token possibly lost or changed)", key);
            }
        }
    }

    /** Updates ticket status under a distributed lock. */
    public Ticket updateStatus(UUID id, TicketStatus status) {
        var key = "lock:ticket:" + id;
        var ttl = Duration.ofMillis(props.getTtlMs());
        var tokenOpt = locks.tryLockWithRetry(key, ttl, Duration.ofMillis(300));
        if (tokenOpt.isEmpty()) {
            throw new ConflictException("Ticket is locked by another process");
        }
        var token = tokenOpt.get();
        try {
            var t = repo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found: " + id));
            t.updateStatus(status);
            return repo.save(t);
        } finally {
            locks.unlock(key, token);
        }
    }
}
