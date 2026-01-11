package com.yourorg.securesaas.domain.workspace;

import com.yourorg.securesaas.domain.AuditableEntity;
import com.yourorg.securesaas.domain.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "workspace_memberships")
public class WorkspaceMembership extends AuditableEntity {

  @Id
  private UUID id;

  @Column(name = "workspace_id", nullable = false)
  private UUID workspaceId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 40)
  private Role role;

  protected WorkspaceMembership() {}

  public WorkspaceMembership(UUID id, UUID workspaceId, UUID userId, Role role) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.userId = userId;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkspaceId() {
    return workspaceId;
  }

  public UUID getUserId() {
    return userId;
  }

  public Role getRole() {
    return role;
  }
}
