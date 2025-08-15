package com.sporty.ticketing.itests;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.boot.web.client.*;
import org.springframework.http.client.*;
import org.springframework.test.context.*;

/**
 * Base class for integration tests.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Boots the Spring Boot application on a random port.</li>
 *   <li>Starts a Redis Testcontainer (configured in {@link RedisTC}).</li>
 *   <li>Registers dynamic Spring properties so the application uses
 *       the container's Redis host and mapped port.</li>
 *   <li>Provides a preconfigured {@link TestRestTemplate} that supports HTTP PATCH.</li>
 * </ul>
 * </p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    /**
     * HTTP client for making requests to the running application under test.
     * Uses Apache HttpComponents to support HTTP PATCH methods.
     */
    final TestRestTemplate http = new TestRestTemplate(
            new RestTemplateBuilder().requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
    );

    /**
     * Configures Spring Boot to use the Redis Testcontainer's host and mapped port.
     */
    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry r) {
        r.add("spring.data.redis.host", () -> RedisTC.REDIS.getHost());
        r.add("spring.data.redis.port", () -> RedisTC.REDIS.getMappedPort(RedisTC.REDIS_PORT));
    }

    /**
     * Builds a full URL for the given path, targeting the running application.
     *
     * @param path relative path (starting with '/')
     * @return full HTTP URL including host and random port
     */
    protected String url(String path) {
        return "http://localhost:" + port + path;
    }
}
