package com.sporty.ticketing.lock;

import java.time.Duration;
import java.util.Optional;

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
     * @param key lock key
     * @param token unique owner token returned by tryLock
     * @return true if lock was released, false if not owned or missing
     */
    boolean unlock(String key, String token);

    /**
     * Helper that retries to acquire a lock until deadline.
     * @param key redis key for the lock
     * @param ttl lock TTL
     * @param maxWait how long to wait
     * @return token if acquired within deadline, otherwise empty
     */
    default Optional<String> tryLockWithRetry(String key, Duration ttl, Duration maxWait) {
        final long deadline = System.nanoTime() + maxWait.toNanos();
        long sleep = 10; // start with 10 ms backoff
        while (System.nanoTime() < deadline) {
            var token = tryLock(key, ttl);
            if (token.isPresent()) return token;
            try {
                Thread.sleep(Math.min(sleep, 100)); // cap backoff
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
            // linear backoff with jitter
            sleep = Math.min(sleep + 10, 100);
        }
        return Optional.empty();
    }
}
