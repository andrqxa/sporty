package com.sporty.ticketing.lock;

import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.*;

/** Wiring for the lock manager. */
@Configuration
public class LockConfig {

  /**
   * Expose a LockManager bean backed by Redis.
   * @param stringRedisTemplate
   * @return LockManager
   **/
  @Bean
  public LockManager lockManager(StringRedisTemplate stringRedisTemplate) {
    return new RedisLockManager(stringRedisTemplate);
  }
}
