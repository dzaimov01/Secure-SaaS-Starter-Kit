package com.yourorg.securesaas.domain.security;

import com.yourorg.securesaas.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
public class UserRole extends AuditableEntity {

  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 40)
  private Role role;

  protected UserRole() {}

  public UserRole(UUID id, UUID userId, Role role) {
    this.id = id;
    this.userId = userId;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public Role getRole() {
    return role;
  }
}
