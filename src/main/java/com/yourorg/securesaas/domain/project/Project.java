package com.yourorg.securesaas.domain.project;

import com.yourorg.securesaas.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project extends AuditableEntity {

  @Id
  private UUID id;

  @Column(name = "workspace_id", nullable = false)
  private UUID workspaceId;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(length = 1000)
  private String description;

  protected Project() {}

  public Project(UUID id, UUID workspaceId, String name, String description) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.name = name;
    this.description = description;
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

  public String getDescription() {
    return description;
  }

  public void update(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
