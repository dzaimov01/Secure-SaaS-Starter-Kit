package com.yourorg.securesaas.infra.rate;

import java.time.Duration;

public interface RateLimiterService {
  boolean allow(String key, int limit, Duration window);
}
