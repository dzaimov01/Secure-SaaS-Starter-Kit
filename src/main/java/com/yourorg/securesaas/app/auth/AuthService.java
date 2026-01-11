package com.yourorg.securesaas.app.auth;

import com.yourorg.securesaas.config.AppProperties;
import com.yourorg.securesaas.domain.auth.RefreshToken;
import com.yourorg.securesaas.domain.auth.User;
import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.security.UserRole;
import com.yourorg.securesaas.domain.workspace.Workspace;
import com.yourorg.securesaas.domain.workspace.WorkspaceMembership;
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
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final WorkspaceRepository workspaceRepository;
  private final WorkspaceMembershipRepository membershipRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final TokenGenerator tokenGenerator;
  private final TokenHasher tokenHasher;
  private final AppProperties properties;

  public AuthService(
      UserRepository userRepository,
      UserRoleRepository userRoleRepository,
      WorkspaceRepository workspaceRepository,
      WorkspaceMembershipRepository membershipRepository,
      RefreshTokenRepository refreshTokenRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      TokenGenerator tokenGenerator,
      TokenHasher tokenHasher,
      AppProperties properties) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.workspaceRepository = workspaceRepository;
    this.membershipRepository = membershipRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.tokenGenerator = tokenGenerator;
    this.tokenHasher = tokenHasher;
    this.properties = properties;
  }

  @Transactional
  public AuthTokens register(String email, String password, String workspaceName) {
    userRepository
        .findByEmail(email.toLowerCase())
        .ifPresent(
            existing -> {
              throw new IllegalArgumentException("email already registered");
            });

    User user = new User(UUID.randomUUID(), email, passwordEncoder.encode(password));
    userRepository.save(user);

    userRoleRepository.save(new UserRole(UUID.randomUUID(), user.getId(), Role.MEMBER));

    Workspace workspace = new Workspace(UUID.randomUUID(), workspaceName, user.getId());
    workspaceRepository.save(workspace);
    membershipRepository.save(
        new WorkspaceMembership(UUID.randomUUID(), workspace.getId(), user.getId(), Role.OWNER));

    return issueTokens(user);
  }

  @Transactional
  public AuthTokens login(String email, String password, String ip, String userAgent) {
    User user =
        userRepository
            .findByEmail(email.toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));

    if (user.isLocked()) {
      throw new IllegalStateException("account locked");
    }

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      user.incrementFailedLogins();
      if (user.getFailedLoginCount() >= properties.getSecurity().getLockout().getMaxAttempts()) {
        user.lockUntil(OffsetDateTime.now().plus(properties.getSecurity().getLockout().getDuration()));
      }
      userRepository.save(user);
      throw new IllegalArgumentException("invalid credentials");
    }

    user.resetFailedLogins();
    userRepository.save(user);
    return issueTokens(user, ip, userAgent);
  }

  @Transactional
  public AuthTokens refresh(String refreshTokenRaw, String ip, String userAgent) {
    RefreshToken token =
        refreshTokenRepository
            .findByTokenHash(tokenHasher.sha256(refreshTokenRaw))
            .orElseThrow(() -> new IllegalArgumentException("invalid refresh token"));

    if (token.isRevoked() || token.isExpired()) {
      throw new IllegalArgumentException("invalid refresh token");
    }

    User user =
        userRepository
            .findById(token.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("invalid refresh token"));

    AuthTokens newTokens = issueTokens(user, ip, userAgent);
    RefreshToken newRefreshToken =
        refreshTokenRepository
            .findByTokenHash(tokenHasher.sha256(newTokens.refreshToken()))
            .orElseThrow();
    token.revoke(newRefreshToken.getId());
    refreshTokenRepository.save(token);

    return newTokens;
  }

  @Transactional
  public void logout(String refreshTokenRaw) {
    refreshTokenRepository
        .findByTokenHash(tokenHasher.sha256(refreshTokenRaw))
        .ifPresent(
            token -> {
              token.revoke(null);
              refreshTokenRepository.save(token);
            });
  }

  private AuthTokens issueTokens(User user) {
    return issueTokens(user, null, null);
  }

  private AuthTokens issueTokens(User user, String ip, String userAgent) {
    List<String> roles =
        userRoleRepository.findByUserId(user.getId()).stream().map(r -> r.getRole().name()).toList();
    String accessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), roles);

    String refreshTokenRaw = tokenGenerator.generateToken(64);
    OffsetDateTime expiry =
        OffsetDateTime.now().plus(properties.getSecurity().getJwt().getRefreshTokenTtl());
    RefreshToken refreshToken =
        new RefreshToken(
            UUID.randomUUID(),
            user.getId(),
            tokenHasher.sha256(refreshTokenRaw),
            expiry,
            ip,
            userAgent);
    refreshTokenRepository.save(refreshToken);

    return new AuthTokens(accessToken, refreshTokenRaw);
  }
}
