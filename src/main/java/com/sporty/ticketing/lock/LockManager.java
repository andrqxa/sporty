package com.sporty.ticketing.lock;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Minimal distributed lock abstraction.
 */
public interface LockManager {

    /**
     * Try to acquire a lock with the given TTL.
     * @param key redis key for the lock
     * @param ttl time to live
     * @return unique token if lock acquired, otherwise empty
     */
    Optional<String> tryLock(String key, Duration ttl);

    /**
     * Release the lock only if the caller owns it (token matches).
     * Returns {@code true} on successful release.
     * @param key lock key
     * @param token unique owner token returned by tryLock
     * @return true if lock was released, false if not owned or missing
     */
    boolean isUnlock(String key, String token);

    /**
     * Helper that retries to acquire a lock until deadline.
     * @param key redis key for the lock
     * @param ttl lock TTL
     * @param maxWait how long to wait
     * @return token if acquired within deadline, otherwise empty
     */
    default Optional<String> tryLockWithRetry(String key,
                                              Duration ttl,
                                              Duration maxWait) {
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

            // Add up to 5 ms jitter to avoid synchronization of retries
            long jitter = ThreadLocalRandom.current().nextLong(0L, 5_000_000L);

            long waitNanos = sleepNanos + jitter;
            if (maxSleepNanos < waitNanos) {
                waitNanos = maxSleepNanos;
            }

            LockSupport.parkNanos(waitNanos);

            // Linear backoff capped at maxSleepNanos
            sleepNanos += 10_000_000L;
            if (maxSleepNanos < sleepNanos) {
                sleepNanos = maxSleepNanos;
            }
        }
    }
}
