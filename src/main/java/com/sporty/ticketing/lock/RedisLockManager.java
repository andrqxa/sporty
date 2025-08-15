package com.sporty.ticketing.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link LockManager} implementation backed by Redis.
 * <p>
 * Uses Redis {@code SET key value NX PX ttl} for acquiring locks
 * and a Lua script for safe release, ensuring that a lock is only
 * removed by the client that owns it.
 * <p>
 * This approach guarantees that:
 * <ul>
 *   <li>Locks have an automatic expiration (TTL) to prevent deadlocks if the client crashes.</li>
 *   <li>Unlocking is atomic — it deletes the lock only if the stored value (owner token) matches.</li>
 * </ul>
 */
public class RedisLockManager implements LockManager {

    /**
     * Lua script to atomically check the lock owner and delete the lock if it matches.
     * <p>
     * Script logic:
     * <pre>
     * if redis.call('get', KEYS[1]) == ARGV[1] then
     *   return redis.call('del', KEYS[1])
     * else
     *   return 0
     * end
     * </pre>
     */
    private static final String LUA_RELEASE = """
        if redis.call('get', KEYS[1]) == ARGV[1] then
          return redis.call('del', KEYS[1])
        else
          return 0
        end
        """;

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> releaseScript;

    /**
     * Creates a new Redis-based lock manager.
     *
     * @param redis the {@link StringRedisTemplate} to use for Redis commands
     */
    public RedisLockManager(StringRedisTemplate redis) {
        this.redis = redis;
        this.releaseScript = new DefaultRedisScript<>(LUA_RELEASE, Long.class);
    }

    /**
     * Attempts to acquire a lock using {@code SET NX PX}.
     *
     * @param key the Redis key representing the lock
     * @param ttl the lock's time-to-live
     * @return an {@link Optional} containing the generated lock token if acquired,
     *         or an empty Optional if the lock is already held
     */
    @Override
    public Optional<String> tryLock(String key, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(key, token, ttl);
        return Boolean.TRUE.equals(ok) ? Optional.of(token) : Optional.empty();
    }

    /**
     * Releases the lock using the Lua script, ensuring only the owner can release it.
     *
     * @param key   the Redis key representing the lock
     * @param token the token of the client attempting to release the lock
     * @return {@code true} if the lock was successfully released, {@code false} otherwise
     */
    @Override
    public boolean unlock(String key, String token) {
        Long res = redis.execute(releaseScript, List.of(key), token);
        return res != null && res == 1L;
    }
}
