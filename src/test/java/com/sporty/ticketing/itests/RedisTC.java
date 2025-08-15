package com.sporty.ticketing.itests;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@Testcontainers
public final class RedisTC {
    public static final int REDIS_PORT = 6379;
    public static final String REDIS_DOCKER_IMAGE_NAME = "redis:7-alpine";
    public static final int STARTUP_TINEOUT = 30;


    @Container
    public static final GenericContainer<?> REDIS = new GenericContainer<>(REDIS_DOCKER_IMAGE_NAME)
            .withExposedPorts(REDIS_PORT)
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(STARTUP_TINEOUT));

    static {
        REDIS.start(); // стартуем один раз на весь ран
        System.out.println("Redis started on: " + REDIS.getHost() + ":" + REDIS.getMappedPort(REDIS_PORT));
    }

    private RedisTC() {}
}
