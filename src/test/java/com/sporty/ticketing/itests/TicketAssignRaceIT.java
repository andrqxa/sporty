package com.sporty.ticketing.itests;

import com.sporty.ticketing.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.test.annotation.*;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Concurrency race: two agents try to assign the same ticket concurrently.
 * Exactly one must succeed (200), the other must receive 409 Conflict.
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
