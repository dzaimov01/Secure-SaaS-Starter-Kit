package com.yourorg.securesaas.domain.auth;

import com.yourorg.securesaas.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends AuditableEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "failed_login_count", nullable = false)
  private int failedLoginCount;

  @Column(name = "locked_until")
  private OffsetDateTime lockedUntil;

  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  protected User() {}

  public User(UUID id, String email, String passwordHash) {
    this.id = id;
    this.email = email.toLowerCase();
    this.passwordHash = passwordHash;
    this.enabled = true;
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public int getFailedLoginCount() {
    return failedLoginCount;
  }

  public OffsetDateTime getLockedUntil() {
    return lockedUntil;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void incrementFailedLogins() {
    this.failedLoginCount++;
  }

  public void resetFailedLogins() {
    this.failedLoginCount = 0;
  }

  public void lockUntil(OffsetDateTime until) {
    this.lockedUntil = until;
  }

  public boolean isLocked() {
    return lockedUntil != null && lockedUntil.isAfter(OffsetDateTime.now());
  }
}
