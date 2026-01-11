package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.workspace.Workspace;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
  List<Workspace> findByOwnerId(UUID ownerId);
}
