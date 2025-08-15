package com.sporty.ticketing.itests;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

/**
 * Singleton-style holder for a Redis Testcontainer used in integration tests.
 * <p>
 * This class ensures that:
 * <ul>
 *   <li>A Redis container (based on {@value #REDIS_DOCKER_IMAGE_NAME}) is started only once for the entire test run.</li>
 *   <li>The container listens on {@value #REDIS_PORT} inside Docker and exposes a mapped port to the host.</li>
 *   <li>Integration tests can retrieve the container's host and port via {@link #REDIS}.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Usage:
 * <pre>
 * &#64;DynamicPropertySource
 * static void redisProps(DynamicPropertyRegistry registry) {
 *     registry.add("spring.data.redis.host", () -> RedisTC.REDIS.getHost());
 *     registry.add("spring.data.redis.port", () -> RedisTC.REDIS.getMappedPort(RedisTC.REDIS_PORT));
 * }
 * </pre>
 * </p>
 *
 * <p><b>Note:</b> The container is started in a static initializer block to ensure it's available
 * before any Spring context is created.</p>
 */
@Testcontainers
public final class RedisTC {

    /** Redis internal port (inside the container). */
    public static final int REDIS_PORT = 6379;

    /** Docker image used for the Redis container. */
    public static final String REDIS_DOCKER_IMAGE_NAME = "redis:7-alpine";

    /** Startup timeout for the container, in seconds. */
    public static final int STARTUP_TIMEOUT = 30;

    /**
     * The Redis Testcontainer instance.
     * Exposed as a static field so all tests share the same container.
     */
    @Container
    public static final GenericContainer<?> REDIS = new GenericContainer<>(REDIS_DOCKER_IMAGE_NAME)
            .withExposedPorts(REDIS_PORT)
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(STARTUP_TIMEOUT));

    static {
        REDIS.start(); // Start once per test run
        System.out.println("Redis started on: " + REDIS.getHost() + ":" + REDIS.getMappedPort(REDIS_PORT));
    }

    /** Private constructor to prevent instantiation. */
    private RedisTC() {}
}
