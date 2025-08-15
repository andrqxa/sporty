package com.sporty.ticketing.itests;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.boot.web.client.*;
import org.springframework.http.client.*;
import org.springframework.test.context.*;

/**
 * Base class that: - boots the app on a random port - starts a Redis testcontainer - wires Spring
 * to use container's host/port for Redis
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    final TestRestTemplate http = new TestRestTemplate(
            new RestTemplateBuilder().requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
    );

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry r) {
        r.add("spring.data.redis.host", () -> RedisTC.REDIS.getHost());
        r.add("spring.data.redis.port", () -> RedisTC.REDIS.getMappedPort(RedisTC.REDIS_PORT));
    }

    protected String url(String path) {
        return "http://localhost:" + port + path;
    }
}
