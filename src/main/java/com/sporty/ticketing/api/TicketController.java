package com.sporty.ticketing.api;

import com.sporty.ticketing.dto.AssignRequest;
import com.sporty.ticketing.dto.CreateTicketRequest;
import com.sporty.ticketing.dto.TicketResponse;
import com.sporty.ticketing.dto.UpdateStatusRequest;
import com.sporty.ticketing.model.Ticket;
import com.sporty.ticketing.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST API for tickets.
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        Ticket t = service.create(req.userId(), req.subject(), req.description());
        return ResponseEntity.created(URI.create("/tickets/" + t.getTicketId()))
                .body(TicketResponse.from(t));
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateStatus(@PathVariable UUID ticketId,
                                                       @Valid @RequestBody UpdateStatusRequest req) {
        var t = service.updateStatus(ticketId, req.status());
        return ResponseEntity.ok(TicketResponse.from(t));
    }

    @PatchMapping("/{ticketId}/assign")
    public ResponseEntity<TicketResponse> assign(@PathVariable UUID ticketId,
                                                 @Valid @RequestBody AssignRequest req) {
        var t = service.assign(ticketId, req.assigneeId());
        return ResponseEntity.ok(TicketResponse.from(t));
    }
}
