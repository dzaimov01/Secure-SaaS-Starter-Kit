package com.yourorg.securesaas.api.auth;

import com.yourorg.securesaas.app.auth.AuthService;
import com.yourorg.securesaas.app.auth.AuthTokens;
import com.yourorg.securesaas.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final AppProperties properties;

  public AuthController(AuthService authService, AppProperties properties) {
    this.authService = authService;
    this.properties = properties;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
      @Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
    AuthTokens tokens =
        authService.register(request.email(), request.password(), request.workspaceName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AuthResponse(tokens.accessToken(), tokens.refreshToken()));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
    AuthTokens tokens =
        authService.login(
            request.email(), request.password(), resolveIp(httpRequest), httpRequest.getHeader("User-Agent"));
    return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
      @Valid @RequestBody RefreshRequest request, HttpServletRequest httpRequest) {
    AuthTokens tokens =
        authService.refresh(request.refreshToken(), resolveIp(httpRequest), httpRequest.getHeader("User-Agent"));
    return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
    authService.logout(request.refreshToken());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/oauth/google")
  public ResponseEntity<Void> oauthGoogle(@Valid @RequestBody OAuthLoginRequest request) {
    if (!properties.getSecurity().getOauth().isGoogleEnabled()) {
      return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  private String resolveIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
