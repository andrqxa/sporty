package com.sporty.ticketing.lock;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;

/**
 * Abstraction for a minimal distributed lock mechanism.
 * <p>
 * Implementations provide a way to acquire and release locks across distributed
 * application instances, typically backed by a shared storage like Redis.
 * Locks are identified by a key and are associated with a unique token
 * that represents the owner of the lock.
 */
public interface LockManager {

    /**
     * Attempts to acquire a lock for the specified key with a given time-to-live (TTL).
     *
     * @param key the lock identifier (e.g., Redis key)
     * @param ttl the lock's time-to-live duration
     * @return an {@link Optional} containing a unique lock token if acquired successfully,
     *         or an empty Optional if the lock could not be obtained
     */
    Optional<String> tryLock(String key, Duration ttl);

    /**
     * Releases the lock for the specified key only if the provided token matches the
     * current lock owner.
     *
     * @param key   the lock identifier
     * @param token the unique token previously returned by {@link #tryLock(String, Duration)}
     * @return {@code true} if the lock was released successfully,
     *         {@code false} if the token does not match or the lock no longer exists
     */
    //noinspection BooleanMethodNameMustStartWithQuestion
    boolean unlock(String key, String token);

    /**
     * Attempts to acquire a lock for the specified key with retries until a deadline is reached.
     * <p>
     * Uses linear backoff with jitter between retries to reduce contention.
     *
     * @param key     the lock identifier
     * @param ttl     the lock's time-to-live duration
     * @param maxWait the maximum time to keep retrying before giving up
     * @return an {@link Optional} containing the lock token if acquired within the deadline,
     *         or an empty Optional if the lock could not be obtained in time
     */
    default Optional<String> tryLockWithRetry(String key, Duration ttl, Duration maxWait) {
        long deadlineNanos = System.nanoTime() + maxWait.toNanos();

        long sleepNanos = 10_000_000L;     // 10 ms
        final long maxSleepNanos = 100_000_000L; // 100 ms

        while (true) {
            long now = System.nanoTime();
            if (now >= deadlineNanos) {
                return Optional.empty();
            }

            var token = tryLock(key, ttl);
            if (token.isPresent()) {
                return token;
            }

            // Add up to 5 ms jitter to avoid retry storms
            long jitter = ThreadLocalRandom.current().nextLong(0L, 5_000_000L);

            long waitNanos = Math.min(sleepNanos + jitter, maxSleepNanos);
            LockSupport.parkNanos(waitNanos);

            // Linear backoff, capped at maxSleepNanos
            sleepNanos = Math.min(sleepNanos + 10_000_000L, maxSleepNanos);
        }
    }
}
