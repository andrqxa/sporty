package com.sporty.ticketing.it;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.junit.jupiter.Container;

/**
 * Base class that:
 *  - boots the app on a random port
 *  - starts a Redis testcontainer
 *  - wires Spring to use container's host/port for Redis
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    protected final TestRestTemplate http = new TestRestTemplate();

    // Lightweight Redis container (official image)
    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> REDIS.getHost());
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    protected String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeAll
    void waitUntilReady() {
        // no-op; container starts automatically, but method kept for clarity
    }
}
