package com.sporty.ticketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis configuration using Lettuce client.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a StringRedisTemplate backed by Lettuce.
     * Spring will auto-configure host/port from spring.data.redis.* properties.
     *
     * @param connectionFactory LettuceConnectionFactory autoconfigured by Spring Boot
     * @return StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
