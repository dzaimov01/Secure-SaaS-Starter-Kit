package com.yourorg.securesaas.domain.invitation;

import com.yourorg.securesaas.domain.AuditableEntity;
import com.yourorg.securesaas.domain.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitations")
public class Invitation extends AuditableEntity {

  @Id
  private UUID id;

  @Column(name = "workspace_id", nullable = false)
  private UUID workspaceId;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(name = "token_hash", nullable = false, length = 255)
  private String tokenHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 40)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 40)
  private InvitationStatus status;

  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Column(name = "accepted_at")
  private OffsetDateTime acceptedAt;

  protected Invitation() {}

  public Invitation(
      UUID id,
      UUID workspaceId,
      String email,
      String tokenHash,
      Role role,
      OffsetDateTime expiresAt) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.email = email.toLowerCase();
    this.tokenHash = tokenHash;
    this.role = role;
    this.status = InvitationStatus.PENDING;
    this.expiresAt = expiresAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkspaceId() {
    return workspaceId;
  }

  public String getEmail() {
    return email;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Role getRole() {
    return role;
  }

  public InvitationStatus getStatus() {
    return status;
  }

  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public OffsetDateTime getAcceptedAt() {
    return acceptedAt;
  }

  public boolean isExpired() {
    return expiresAt.isBefore(OffsetDateTime.now());
  }

  public void accept() {
    this.status = InvitationStatus.ACCEPTED;
    this.acceptedAt = OffsetDateTime.now();
  }
}
