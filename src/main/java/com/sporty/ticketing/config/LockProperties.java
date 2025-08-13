package com.sporty.ticketing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application-specific lock properties.
 */
@ConfigurationProperties(prefix = "app.lock")
public class LockProperties {

    /**
     * Default TTL for Redis-based locks in milliseconds.
     */
    private long ttlMs = 5000;

    public long getTtlMs() {
        return ttlMs;
    }

    public void setTtlMs(long ttlMs) {
        this.ttlMs = ttlMs;
    }
}
