package com.yourorg.securesaas.infra.rate;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.rate-limit.redis-enabled", havingValue = "true")
public class RedisRateLimiterService implements RateLimiterService {

  private final StringRedisTemplate redisTemplate;

  public RedisRateLimiterService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public boolean allow(String key, int limit, Duration window) {
    Long count = redisTemplate.opsForValue().increment(key);
    if (count != null && count == 1L) {
      redisTemplate.expire(key, window);
    }
    return count != null && count <= limit;
  }
}
