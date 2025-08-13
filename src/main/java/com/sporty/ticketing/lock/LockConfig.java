package com.sporty.ticketing.lock;

import com.sporty.ticketing.config.LockProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/** Wiring for the lock manager. */
@Configuration
public class LockConfig {

  /** Expose a LockManager bean backed by Redis. */
  @Bean
  public LockManager lockManager(StringRedisTemplate stringRedisTemplate) {
    return new RedisLockManager(stringRedisTemplate);
  }

  /** Just to ensure LockProperties is bound and available if needed elsewhere. */
  @Bean
  public LockProperties lockProperties() {
    return new LockProperties();
  }
}
