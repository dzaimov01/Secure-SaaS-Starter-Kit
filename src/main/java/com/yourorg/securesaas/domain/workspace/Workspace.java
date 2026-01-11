package com.yourorg.securesaas.domain.workspace;

import com.yourorg.securesaas.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
public class Workspace extends AuditableEntity {

  @Id
  private UUID id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  protected Workspace() {}

  public Workspace(UUID id, String name, UUID ownerId) {
    this.id = id;
    this.name = name;
    this.ownerId = ownerId;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UUID getOwnerId() {
    return ownerId;
  }
}
