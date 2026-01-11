package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.project.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
  List<Project> findByWorkspaceId(UUID workspaceId);

  Optional<Project> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
