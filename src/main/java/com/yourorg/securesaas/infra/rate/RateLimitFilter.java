package com.yourorg.securesaas.infra.rate;

import com.yourorg.securesaas.config.AppProperties;
import com.yourorg.securesaas.infra.security.AuthenticatedPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private final RateLimiterService rateLimiterService;
  private final AppProperties properties;

  public RateLimitFilter(RateLimiterService rateLimiterService, AppProperties properties) {
    this.rateLimiterService = rateLimiterService;
    this.properties = properties;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!properties.getRateLimit().isEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    String path = request.getRequestURI();
    String ip = resolveIp(request);

    boolean isAuth = path.startsWith("/auth/");
    AppProperties.RateLimit.Limit limit =
        isAuth ? properties.getRateLimit().getAuth() : properties.getRateLimit().getApi();

    String keyPrefix = isAuth ? "auth" : "api";
    String ipKey = keyPrefix + ":ip:" + ip;

    if (!rateLimiterService.allow(ipKey, limit.getLimit(), limit.getWindow())) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      return;
    }

    Optional<String> userKey = resolveUserKey();
    if (userKey.isPresent()) {
      String key = keyPrefix + ":user:" + userKey.get();
      if (!rateLimiterService.allow(key, limit.getLimit(), limit.getWindow())) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private Optional<String> resolveUserKey() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof AuthenticatedPrincipal authPrincipal) {
      if (authPrincipal.getUserId() != null) {
        return Optional.of(authPrincipal.getUserId().toString());
      }
    }
    return Optional.empty();
  }
}
