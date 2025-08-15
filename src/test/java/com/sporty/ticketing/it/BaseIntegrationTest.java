package com.sporty.ticketing.it;

/**
 * Base class that: - boots the app on a random port - starts a Redis testcontainer - wires Spring
 * to use container's host/port for Redis
 */

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.springframework.boot.web.client.*;
import org.springframework.http.client.*;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.junit.jupiter.Container;

import java.time.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    public static final int REDIS_PORT = 6379;
    public static final String REDIS_DOCKER_IMAGE_NAME = "redis:7-alpine";
    public static final int STARTUP_TINEOUT = 30;

    @Container
    @ServiceConnection
    public static final GenericContainer<?> REDIS = new GenericContainer<>(REDIS_DOCKER_IMAGE_NAME)
            .withExposedPorts(REDIS_PORT)
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(STARTUP_TINEOUT));

    static {
        REDIS.start();
        System.out.println("Redis started on: " + REDIS.getHost() + ":" + REDIS.getMappedPort(REDIS_PORT));
    }

    @LocalServerPort
    protected int port;

    protected final TestRestTemplate http = new TestRestTemplate(
            new RestTemplateBuilder().requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
    );

    protected String url(String path) {
        return "http://localhost:" + port + path;
    }
}
