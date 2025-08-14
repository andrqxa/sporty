package com.sporty.ticketing.it;

import com.sporty.ticketing.dto.AssignRequest;
import com.sporty.ticketing.dto.CreateTicketRequest;
import com.sporty.ticketing.dto.TicketResponse;
import com.sporty.ticketing.dto.UpdateStatusRequest;
import com.sporty.ticketing.model.TicketStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Basic end-to-end flow: create -> assign -> update status.
 */
public class TicketHappyPathIT extends BaseIntegrationTest {

    @Test
    void create_assign_updateStatus() {
        // create
        var createReq = new CreateTicketRequest("user-1", "Login fails", "...");
        ResponseEntity<TicketResponse> created =
                http.postForEntity(url("/tickets"), createReq, TicketResponse.class);

        Assertions.assertThat(created.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(created.getBody()).isNotNull();
        UUID id = created.getBody().ticketId();

        // assign
        var assignReq = new AssignRequest("agent-1");
        ResponseEntity<TicketResponse> assigned = http.exchange(
                url("/tickets/" + id + "/assign"),
                HttpMethod.PATCH,
                new HttpEntity<>(assignReq),
                TicketResponse.class);

        Assertions.assertThat(assigned.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(assigned.getBody()).isNotNull();
        Assertions.assertThat(assigned.getBody().assigneeId()).isEqualTo("agent-1");

        // update status
        var statusReq = new UpdateStatusRequest(TicketStatus.IN_PROGRESS);
        ResponseEntity<TicketResponse> updated = http.exchange(
                url("/tickets/" + id + "/status"),
                HttpMethod.PATCH,
                new HttpEntity<>(statusReq),
                TicketResponse.class);

        Assertions.assertThat(updated.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(updated.getBody()).isNotNull();
        Assertions.assertThat(updated.getBody().status()).isEqualTo(TicketStatus.IN_PROGRESS);
    }
}
