package com.sporty.ticketing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for application-level distributed locks.
 * <p>
 * These properties are used to configure the default settings for Redis-based locks
 * that are applied in concurrent operations within the application.
 * <p>
 * Properties are loaded from the configuration using the prefix {@code app.lock}.
 * Example configuration in {@code application.yml}:
 * <pre>
 * app:
 *   lock:
 *     ttl-ms: 5000
 * </pre>
 */
@ConfigurationProperties(prefix = "app.lock")
public class LockProperties {

    /**
     * Default Time-To-Live (TTL) for Redis-based locks, in milliseconds.
     * <p>
     * This value determines how long a lock will remain valid before being automatically released.
     * Defaults to {@code 5000} milliseconds (5 seconds).
     */
    private long ttlMs = 5000;

    /**
     * Returns the current default TTL for Redis-based locks in milliseconds.
     *
     * @return the TTL in milliseconds
     */
    public long getTtlMs() {
        return ttlMs;
    }

    /**
     * Sets the default TTL for Redis-based locks in milliseconds.
     *
     * @param ttlMs the TTL in milliseconds
     */
    public void setTtlMs(long ttlMs) {
        this.ttlMs = ttlMs;
    }
}
