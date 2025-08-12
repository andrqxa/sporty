# Ticketing System (Distributed Locking)

A minimal Java 21 / Spring Boot 3.3 backend that manages support tickets and guarantees consistency under concurrent updates using a Redis-backed distributed lock. The scope mirrors the 90-minute assignment: REST endpoints to create/modify tickets, plus a robust locking strategy to avoid races between multiple service instances.&#x20;

## Table of Contents

* [Goals](#goals)
* [Architecture & Stack](#architecture--stack)
* [Data Model](#data-model)
* [Locking Strategy](#locking-strategy)
* [Running the Project](#running-the-project)
* [API](#api)
* [Errors & Status Codes](#errors--status-codes)
* [Concurrent Update Test](#concurrent-update-test)
* [Limitations & Next Steps](#limitations--next-steps)
* [AI Usage & Validation](#ai-usage--validation)
* [Assignment References](#assignment-references)

---

## Goals

* Provide REST endpoints to:

  * create a support ticket,
  * update ticket status,
  * assign a ticket to an agent.&#x20;
* Ensure **distributed mutual exclusion** so that concurrent updates to the same ticket (across multiple app instances) remain consistent. Example: two agents try to assign the same ticket at once — only one should succeed.&#x20;
* Store tickets in an **in-memory repository** for the scope of this assignment.&#x20;

## Architecture & Stack

* **Language/Runtime:** Java 21
* **Framework:** Spring Boot 3.3.x (Web, Validation)
* **Distributed lock:** Spring Data Redis (Lettuce client)
* **Storage:** In-memory `ConcurrentHashMap` (mock persistence)
* **Tests:** JUnit 5, AssertJ

Logical packages:

* `api` — REST controllers
* `service` — domain logic (critical sections wrapped with distributed locks)
* `repo` — in-memory repository
* `lock` — Redis lock manager (acquire/release)
* `config` — application & Redis configuration

## Data Model

Ticket fields (as per assignment):

* `ticketId: UUID`
* `subject: String`
* `description: String`
* `status: enum { open, in_progress, resolved, closed }`
* `userId: String`
* `assigneeId: String | null`
* `createdAt: Instant`
* `updatedAt: Instant`&#x20;

**Locking requirement:** only one process may update a given ticket at a time. If two agents try to assign themselves concurrently, exactly one wins.&#x20;

## Locking Strategy

We use a **Redis single-key lock per ticket**:

1. **Acquire** (atomic):

```
SET lock:ticket:{ticketId} {token} NX PX {ttl_ms}
```

* `NX`: set only if the key doesn’t exist (no one else holds the lock).
* `PX {ttl}`: lock auto-expires to avoid deadlocks if the owner dies.
* `{token}`: random UUID identifying the lock owner.

2. **Critical section**:

* Proceed only when `SET ... NX` returns `OK`.
* If not acquired within a small deadline (with jitter), return `409 Conflict` (or `423 Locked`) to the client.

3. **Safe release** (atomic “compare-and-delete”):

* Use a small Lua script to delete **only if** the stored token matches the owner’s token:

  ```lua
  if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
  else
    return 0
  end
  ```

This prevents accidentally removing a lock that another process obtained between a `GET` and `DEL`. (The assignment allows a Redis-based lock; this approach is the minimal, reliable pattern.)&#x20;

**Operational notes**

* Pick `TTL` to cover worst-case operation time (or implement a watchdog/extension in longer tasks).
* Apply retry with backoff/jitter; cap total wait time to keep API latency predictable.

## Running the Project

### Prerequisites

* JDK 21+
* Maven 3.9+
* Docker (for Redis)

### Start Redis

Using Docker:

```bash
docker run -p 6379:6379 --name redis -d redis:7-alpine
# or: docker compose up -d
```

### Configure & Run App

`application.yml` (example):

```yaml
server:
  port: 8080

app:
  lock:
    ttl-ms: 5000

spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

Run:

```bash
./mvnw spring-boot:run
```

Optional env vars:

```
REDIS_HOST=localhost
REDIS_PORT=6379
LOCK_TTL_MS=5000
```

## API

The API surface follows the assignment specification. Payloads below are examples.&#x20;

### 1) Create Ticket

`POST /tickets`

```json
{
  "userId": "user-001",
  "subject": "Login not working",
  "description": "I can’t sign in to my account."
}
```

**Response:** `201 Created` + ticket JSON.&#x20;

Example:

```bash
curl -sS -X POST localhost:8080/tickets \
  -H 'Content-Type: application/json' \
  -d '{"userId":"user-001","subject":"Login not working","description":"I can’t sign in"}'
```

### 2) Update Ticket Status

`PATCH /tickets/{ticketId}/status`

```json
{ "status": "resolved" }
```

**Response:** `200 OK` + updated ticket.&#x20;

Example:

```bash
curl -sS -X PATCH localhost:8080/tickets/{id}/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"in_progress"}'
```

### 3) Assign Ticket

`PATCH /tickets/{ticketId}/assign`

```json
{ "assigneeId": "agent-123" }
```

**Response:** `200 OK` + updated ticket.
On contention, expect `409 Conflict` (or `423 Locked`) for the losing request.&#x20;

Example:

```bash
curl -sS -X PATCH localhost:8080/tickets/{id}/assign \
  -H 'Content-Type: application/json' \
  -d '{"assigneeId":"agent-123"}'
```

## Errors & Status Codes

* `400 Bad Request` — validation errors
* `404 Not Found` — ticket not found
* `409 Conflict` — concurrent update conflict / lock could not be acquired
* `423 Locked` — alternative to 409 when the resource is locked
* `500 Internal Server Error` — unexpected errors

## Concurrent Update Test

The project includes an integration test that simulates a race:

* Two threads (via `ExecutorService` + `CountDownLatch`) attempt to assign the same ticket concurrently.
* Each service method first tries to acquire `lock:ticket:{id}` with a short deadline.
* **Expected outcome:** one `200 OK`, one `409 Conflict`; the repository ends up with a single `assigneeId` set.&#x20;

Run:

```bash
./mvnw -Dtest=*Concurrency* test
```

## Limitations & Next Steps

* **In-memory store** is non-persistent; for production, use a database (e.g., Postgres) with optimistic concurrency (versioning) in addition to distributed locks.
* **Single Redis node** is enough for this assignment; in production consider Redis Cluster/replication or multi-node strategies (e.g., Redlock) depending on SLAs.
* Locks are **non-fair**; consider backoff/jitter and request-level timeouts to reduce starvation.

## AI Usage & Validation

* AI assistance was used to draft the README and implementation outline.
* Validation steps:

  * Manual `curl` checks of all endpoints.
  * Integration test to verify contention behavior.
  * Review of lock TTL and safe release logic (token-checked, atomic).

## Assignment References

* Overview and goals; Java/Spring Boot focus; distributed coordination & locking.&#x20;
* Functional Requirements; in-memory storage; concurrency safety.&#x20;
* Data model and statuses.&#x20;
* API specification (create, update status, assign).&#x20;
* Delivery checklist (GitHub repo, README contents, optional docker compose, AI usage).&#x20;

