package com.yourorg.securesaas.domain.auth;

import com.yourorg.securesaas.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends AuditableEntity {

  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "token_hash", nullable = false, length = 255)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Column(name = "revoked_at")
  private OffsetDateTime revokedAt;

  @Column(name = "replaced_by_token_id")
  private UUID replacedByTokenId;

  @Column(name = "ip_address", length = 64)
  private String ipAddress;

  @Column(name = "user_agent", length = 255)
  private String userAgent;

  protected RefreshToken() {}

  public RefreshToken(
      UUID id,
      UUID userId,
      String tokenHash,
      OffsetDateTime expiresAt,
      String ipAddress,
      String userAgent) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public OffsetDateTime getRevokedAt() {
    return revokedAt;
  }

  public UUID getReplacedByTokenId() {
    return replacedByTokenId;
  }

  public boolean isExpired() {
    return expiresAt.isBefore(OffsetDateTime.now());
  }

  public boolean isRevoked() {
    return revokedAt != null;
  }

  public void revoke(UUID replacedByTokenId) {
    this.revokedAt = OffsetDateTime.now();
    this.replacedByTokenId = replacedByTokenId;
  }
}
