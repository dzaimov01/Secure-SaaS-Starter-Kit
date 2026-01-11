package com.yourorg.securesaas.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yourorg.securesaas.app.auth.AuthService;
import com.yourorg.securesaas.app.auth.AuthTokens;
import com.yourorg.securesaas.config.AppProperties;
import com.yourorg.securesaas.domain.auth.RefreshToken;
import com.yourorg.securesaas.domain.auth.User;
import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.security.UserRole;
import com.yourorg.securesaas.infra.db.RefreshTokenRepository;
import com.yourorg.securesaas.infra.db.UserRepository;
import com.yourorg.securesaas.infra.db.UserRoleRepository;
import com.yourorg.securesaas.infra.db.WorkspaceMembershipRepository;
import com.yourorg.securesaas.infra.db.WorkspaceRepository;
import com.yourorg.securesaas.infra.security.JwtService;
import com.yourorg.securesaas.infra.security.TokenGenerator;
import com.yourorg.securesaas.infra.security.TokenHasher;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class TokenRotationTest {

  private AuthService authService;
  private RefreshTokenRepository refreshTokenRepository;
  private TokenHasher tokenHasher;

  @BeforeEach
  void setup() {
    UserRepository userRepository = mock(UserRepository.class);
    UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);
    WorkspaceRepository workspaceRepository = mock(WorkspaceRepository.class);
    WorkspaceMembershipRepository membershipRepository = mock(WorkspaceMembershipRepository.class);
    refreshTokenRepository = mock(RefreshTokenRepository.class);
    JwtService jwtService = mock(JwtService.class);
    TokenGenerator tokenGenerator = mock(TokenGenerator.class);
    tokenHasher = new TokenHasher();

    AppProperties properties = new AppProperties();
    properties.getSecurity().getJwt().setRefreshTokenTtl(java.time.Duration.ofMinutes(30));

    authService =
        new AuthService(
            userRepository,
            userRoleRepository,
            workspaceRepository,
            membershipRepository,
            refreshTokenRepository,
            new BCryptPasswordEncoder(),
            jwtService,
            tokenGenerator,
            tokenHasher,
            properties);

    UUID userId = UUID.randomUUID();
    User user = new User(userId, "user@example.com", "hash");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRoleRepository.findByUserId(userId))
        .thenReturn(List.of(new UserRole(UUID.randomUUID(), userId, Role.MEMBER)));
    when(jwtService.issueAccessToken(any(), any(), any())).thenReturn("access-token");
    when(tokenGenerator.generateToken(Mockito.anyInt())).thenReturn("new-refresh");

    RefreshToken existing =
        new RefreshToken(
            UUID.randomUUID(),
            userId,
            tokenHasher.sha256("old-refresh"),
            OffsetDateTime.now().plusMinutes(10),
            "1.2.3.4",
            "agent");
    when(refreshTokenRepository.findByTokenHash(tokenHasher.sha256("old-refresh")))
        .thenReturn(Optional.of(existing));

    RefreshToken newToken =
        new RefreshToken(
            UUID.randomUUID(),
            userId,
            tokenHasher.sha256("new-refresh"),
            OffsetDateTime.now().plusMinutes(10),
            "1.2.3.4",
            "agent");
    when(refreshTokenRepository.findByTokenHash(tokenHasher.sha256("new-refresh")))
        .thenReturn(Optional.of(newToken));
  }

  @Test
  void refreshRotatesToken() {
    AuthTokens tokens = authService.refresh("old-refresh", "1.2.3.4", "agent");
    assertThat(tokens.refreshToken()).isEqualTo("new-refresh");
  }
}
