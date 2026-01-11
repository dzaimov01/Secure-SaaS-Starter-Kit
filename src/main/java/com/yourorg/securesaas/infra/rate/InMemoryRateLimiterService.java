package com.yourorg.securesaas.infra.rate;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.rate-limit.redis-enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryRateLimiterService implements RateLimiterService {

  private final Map<String, Window> windows = new ConcurrentHashMap<>();

  @Override
  public boolean allow(String key, int limit, Duration windowSize) {
    Window window = windows.computeIfAbsent(key, ignored -> new Window());
    return window.allow(limit, windowSize);
  }

  private static class Window {
    private Instant windowStart = Instant.now();
    private int count = 0;

    synchronized boolean allow(int limit, Duration windowSize) {
      Instant now = Instant.now();
      if (now.isAfter(windowStart.plus(windowSize))) {
        windowStart = now;
        count = 0;
      }
      count++;
      return count <= limit;
    }
  }
}
