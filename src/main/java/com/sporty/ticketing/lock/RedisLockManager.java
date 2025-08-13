package com.sporty.ticketing.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Redis-based distributed lock using SET NX PX + safe Lua unlock.
 */
public class RedisLockManager implements LockManager {

    // Atomic "compare-and-delete" to release only if owner token matches
    private static final String LUA_RELEASE = """
        if redis.call('get', KEYS[1]) == ARGV[1] then
          return redis.call('del', KEYS[1])
        else
          return 0
        end
        """;

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> releaseScript;

    public RedisLockManager(StringRedisTemplate redis) {
        this.redis = redis;
        this.releaseScript = new DefaultRedisScript<>(LUA_RELEASE, Long.class);
    }

    @Override
    public Optional<String> tryLock(String key, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(key, token, ttl);
        return Boolean.TRUE.equals(ok) ? Optional.of(token) : Optional.empty();
    }

    @Override
    public boolean unlock(String key, String token) {
        Long res = redis.execute(releaseScript, List.of(key), token);
        return res != null && 1L == res.longValue();
    }
}
