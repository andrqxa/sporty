package com.sporty.ticketing.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Spring configuration for distributed locking.
 * <p>
 * This class wires the {@link LockManager} bean with a Redis-backed implementation.
 * It uses a {@link StringRedisTemplate} to interact with Redis and manage
 * distributed locks across multiple instances of the application.
 */
@Configuration
public class LockConfig {

    /**
     * Creates and exposes a {@link LockManager} implementation backed by Redis.
     *
     * @param stringRedisTemplate the {@link StringRedisTemplate} used for Redis operations
     * @return a Redis-based {@link LockManager} instance
     */
    @Bean
    public LockManager lockManager(StringRedisTemplate stringRedisTemplate) {
        return new RedisLockManager(stringRedisTemplate);
    }
}
