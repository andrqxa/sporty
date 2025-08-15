package com.sporty.ticketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis configuration for the application using the Lettuce client.
 * <p>
 * This configuration defines the beans required to interact with Redis
 * via Spring Data Redis, specifically the {@link StringRedisTemplate} for
 * working with string-based keys and values.
 * <p>
 * The connection settings (host, port, etc.) are automatically loaded
 * from the {@code spring.data.redis.*} properties defined in the application configuration.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates and configures a {@link StringRedisTemplate} bean backed by Lettuce.
     * <p>
     * The template provides high-level operations for working with Redis string keys and values.
     * <p>
     * Connection details are provided by the {@link LettuceConnectionFactory},
     * which is automatically configured by Spring Boot based on the application properties.
     *
     * @param connectionFactory the Lettuce connection factory configured by Spring Boot
     * @return a fully configured {@link StringRedisTemplate} instance
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
