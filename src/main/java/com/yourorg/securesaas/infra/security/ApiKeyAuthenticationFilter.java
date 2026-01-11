package com.yourorg.securesaas.infra.security;

import com.yourorg.securesaas.domain.apikey.ApiKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-API-Key";

  private final ApiKeyAuthenticator apiKeyAuthenticator;

  public ApiKeyAuthenticationFilter(ApiKeyAuthenticator apiKeyAuthenticator) {
    this.apiKeyAuthenticator = apiKeyAuthenticator;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader(API_KEY_HEADER);
    if (header == null || header.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    apiKeyAuthenticator
        .authenticate(header)
        .ifPresent(
            apiKey -> {
              AuthenticatedPrincipal principal =
                  new AuthenticatedPrincipal(null, null, apiKey.getWorkspaceId(), true);
              UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(
                      principal,
                      apiKey.getId(),
                      List.of(new SimpleGrantedAuthority("ROLE_API_KEY")));
              SecurityContextHolder.getContext().setAuthentication(authentication);
            });

    filterChain.doFilter(request, response);
  }
}
