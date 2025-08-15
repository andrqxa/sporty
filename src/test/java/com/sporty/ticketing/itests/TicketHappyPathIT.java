package com.sporty.ticketing.itests;

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
import org.springframework.test.annotation.*;

import java.util.UUID;

/**
 * Integration test verifying the "happy path" for ticket lifecycle operations.
 *
 * <p>This scenario tests the basic, successful end-to-end flow of the Ticketing API:</p>
 * <ol>
 *   <li>Create a new ticket via {@code POST /tickets}.</li>
 *   <li>Assign the ticket to an agent via {@code PATCH /tickets/{id}/assign}.</li>
 *   <li>Update the ticket status via {@code PATCH /tickets/{id}/status}.</li>
 * </ol>
 *
 * <p>Each step asserts that:</p>
 * <ul>
 *   <li>The HTTP response status is 2xx (success).</li>
 *   <li>The response body is non-null.</li>
 *   <li>The returned ticket representation reflects the expected changes
 *       (correct assignee after assignment, correct status after update).</li>
 * </ul>
 *
 * <p>This test ensures that the main application flow works correctly without
 * concurrency conflicts or invalid input, and that persistence of changes
 * through the repository layer is functioning as expected.</p>
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
