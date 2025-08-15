package com.sporty.ticketing.api;

import com.sporty.ticketing.dto.*;
import com.sporty.ticketing.model.*;
import com.sporty.ticketing.service.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.*;
import java.util.*;

/**
 * REST controller that provides operations for creating and managing support tickets.
 * <p>
 * This controller exposes endpoints for:
 * <ul>
 *   <li>Creating new tickets</li>
 *   <li>Updating ticket status</li>
 *   <li>Assigning tickets to specific users</li>
 * </ul>
 * All request payloads are validated using {@link jakarta.validation.Valid}.
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    /**
     * Creates a new instance of {@code TicketController}.
     *
     * @param service the ticket service used for ticket operations
     */
    public TicketController(TicketService service) {
        this.service = service;
    }

    /**
     * Creates a new ticket.
     *
     * @param req the {@link CreateTicketRequest} containing user ID, subject, and description
     * @return a {@link ResponseEntity} with {@link TicketResponse} and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        Ticket t = service.create(req.userId(), req.subject(), req.description());
        return ResponseEntity.created(URI.create("/tickets/" + t.getTicketId()))
                .body(TicketResponse.from(t));
    }

    /**
     * Updates the status of an existing ticket.
     *
     * @param ticketId the unique identifier of the ticket
     * @param req      the {@link UpdateStatusRequest} containing the new status
     * @return a {@link ResponseEntity} with the updated {@link TicketResponse} and HTTP status 200 (OK)
     */
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateStatus(@PathVariable UUID ticketId,
                                                       @Valid @RequestBody UpdateStatusRequest req) {
        var t = service.updateStatus(ticketId, req.status());
        return ResponseEntity.ok(TicketResponse.from(t));
    }

    /**
     * Assigns a ticket to a specific user.
     *
     * @param ticketId the unique identifier of the ticket
     * @param req      the {@link AssignRequest} containing the assignee ID
     * @return a {@link ResponseEntity} with the updated {@link TicketResponse} and HTTP status 200 (OK)
     */
    @PatchMapping("/{ticketId}/assign")
    public ResponseEntity<TicketResponse> assign(@PathVariable UUID ticketId,
                                                 @Valid @RequestBody AssignRequest req) {
        var t = service.assign(ticketId, req.assigneeId());
        return ResponseEntity.ok(TicketResponse.from(t));
    }
}
