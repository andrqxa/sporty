package com.sporty.ticketing.itests;

import com.sporty.ticketing.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.test.annotation.*;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test verifying distributed locking behavior during concurrent ticket assignment.
 *
 * <p>This scenario simulates a race condition where two different agents attempt to assign
 * the same ticket at exactly the same time. The system uses a Redis-backed {@code LockManager}
 * to ensure that only one of the competing requests succeeds, while the other must fail
 * with HTTP 409 (Conflict).</p>
 *
 * <p>Test flow:</p>
 * <ol>
 *   <li>Create a new ticket via {@code POST /tickets}.</li>
 *   <li>Prepare two {@code PATCH /tickets/{id}/assign} requests with different assignee IDs.</li>
 *   <li>Trigger both requests concurrently using {@link ExecutorService} and {@link CountDownLatch}
 *       to ensure near-simultaneous start.</li>
 *   <li>Verify that exactly one request completes successfully (HTTP 200) and exactly one fails with 409 Conflict.</li>
 *   <li>Fetch the winner response and assert that the assigned agent is either "agent-A" or "agent-B".</li>
 * </ol>
 *
 * <p>This test ensures correctness of concurrency control and verifies that Redis-based locking
 * works as expected under contention.</p>
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TicketAssignRaceIT extends BaseIntegrationTest {

    @Test
    void twoConcurrentAssigns_oneWins_other409() throws Exception {
        // create ticket
        var createReq = new CreateTicketRequest("user-1", "Login fails", "...");
        ResponseEntity<TicketResponse> created =
                http.postForEntity(url("/tickets"), createReq, TicketResponse.class);
        assertThat(created.getStatusCode().is2xxSuccessful()).isTrue();
        UUID id = created.getBody().ticketId();

        // prepare two concurrent PATCH /assign calls
        var reqA = new HttpEntity<>(new AssignRequest("agent-A"));
        var reqB = new HttpEntity<>(new AssignRequest("agent-B"));

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<ResponseEntity<TicketResponse>>> futures = new ArrayList<>();

        Callable<ResponseEntity<TicketResponse>> taskA = () -> {
            start.await();
            return http.exchange(url("/tickets/" + id + "/assign"), HttpMethod.PATCH, reqA, TicketResponse.class);
        };
        Callable<ResponseEntity<TicketResponse>> taskB = () -> {
            start.await();
            return http.exchange(url("/tickets/" + id + "/assign"), HttpMethod.PATCH, reqB, TicketResponse.class);
        };

        futures.add(pool.submit(taskA));
        futures.add(pool.submit(taskB));

        // start both at once
        start.countDown();

        ResponseEntity<TicketResponse> r1 = futures.get(0).get(3, TimeUnit.SECONDS);
        ResponseEntity<TicketResponse> r2 = futures.get(1).get(3, TimeUnit.SECONDS);

        pool.shutdownNow();

        // assert: exactly one OK, one 409
        long ok = List.of(r1, r2).stream().filter(r -> r.getStatusCode().is2xxSuccessful()).count();
        long conflict = List.of(r1, r2).stream().filter(r -> r.getStatusCode().value() == 409).count();

        assertThat(ok).isEqualTo(1);
        assertThat(conflict).isEqualTo(1);

        // fetch the winner to verify single assignee
        ResponseEntity<TicketResponse> winner = r1.getStatusCode().is2xxSuccessful() ? r1 : r2;
        String assigned = winner.getBody().assigneeId();
        assertThat(assigned).isIn("agent-A", "agent-B");
    }
}
