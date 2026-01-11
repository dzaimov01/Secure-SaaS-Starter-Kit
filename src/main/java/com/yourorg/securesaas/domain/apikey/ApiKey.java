package com.yourorg.securesaas.domain.apikey;

import com.yourorg.securesaas.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey extends AuditableEntity {

  @Id
  private UUID id;

  @Column(name = "workspace_id", nullable = false)
  private UUID workspaceId;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 12)
  private String prefix;

  @Column(name = "key_hash", nullable = false, length = 255)
  private String keyHash;

  @Column(name = "revoked_at")
  private OffsetDateTime revokedAt;

  protected ApiKey() {}

  public ApiKey(UUID id, UUID workspaceId, String name, String prefix, String keyHash) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.name = name;
    this.prefix = prefix;
    this.keyHash = keyHash;
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkspaceId() {
    return workspaceId;
  }

  public String getName() {
    return name;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getKeyHash() {
    return keyHash;
  }

  public OffsetDateTime getRevokedAt() {
    return revokedAt;
  }

  public boolean isRevoked() {
    return revokedAt != null;
  }

  public void revoke() {
    this.revokedAt = OffsetDateTime.now();
  }
}
