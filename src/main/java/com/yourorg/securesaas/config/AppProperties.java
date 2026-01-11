package com.yourorg.securesaas.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final Security security = new Security();
  private final RateLimit rateLimit = new RateLimit();
  private final Cors cors = new Cors();

  public Security getSecurity() {
    return security;
  }

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  public Cors getCors() {
    return cors;
  }

  public static class Security {
    private final Jwt jwt = new Jwt();
    private final Lockout lockout = new Lockout();
    private final OAuth oauth = new OAuth();

    public Jwt getJwt() {
      return jwt;
    }

    public Lockout getLockout() {
      return lockout;
    }

    public OAuth getOauth() {
      return oauth;
    }

    public static class Jwt {
      private String issuer;
      private Duration accessTokenTtl = Duration.ofMinutes(15);
      private Duration refreshTokenTtl = Duration.ofDays(30);
      private String signingKey;

      public String getIssuer() {
        return issuer;
      }

      public void setIssuer(String issuer) {
        this.issuer = issuer;
      }

      public Duration getAccessTokenTtl() {
        return accessTokenTtl;
      }

      public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
      }

      public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
      }

      public void setRefreshTokenTtl(Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
      }

      public String getSigningKey() {
        return signingKey;
      }

      public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
      }
    }

    public static class Lockout {
      private int maxAttempts = 5;
      private Duration duration = Duration.ofMinutes(15);

      public int getMaxAttempts() {
        return maxAttempts;
      }

      public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
      }

      public Duration getDuration() {
        return duration;
      }

      public void setDuration(Duration duration) {
        this.duration = duration;
      }
    }

    public static class OAuth {
      private boolean googleEnabled = false;

      public boolean isGoogleEnabled() {
        return googleEnabled;
      }

      public void setGoogleEnabled(boolean googleEnabled) {
        this.googleEnabled = googleEnabled;
      }
    }
  }

  public static class RateLimit {
    private boolean enabled = true;
    private boolean redisEnabled = false;
    private final Limit auth = new Limit();
    private final Limit api = new Limit();

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public boolean isRedisEnabled() {
      return redisEnabled;
    }

    public void setRedisEnabled(boolean redisEnabled) {
      this.redisEnabled = redisEnabled;
    }

    public Limit getAuth() {
      return auth;
    }

    public Limit getApi() {
      return api;
    }

    public static class Limit {
      private int limit = 60;
      private Duration window = Duration.ofMinutes(1);

      public int getLimit() {
        return limit;
      }

      public void setLimit(int limit) {
        this.limit = limit;
      }

      public Duration getWindow() {
        return window;
      }

      public void setWindow(Duration window) {
        this.window = window;
      }
    }
  }

  public static class Cors {
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;

    public String getAllowedOrigins() {
      return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
      this.allowedOrigins = allowedOrigins;
    }

    public String getAllowedMethods() {
      return allowedMethods;
    }

    public void setAllowedMethods(String allowedMethods) {
      this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders() {
      return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
      this.allowedHeaders = allowedHeaders;
    }
  }
}
