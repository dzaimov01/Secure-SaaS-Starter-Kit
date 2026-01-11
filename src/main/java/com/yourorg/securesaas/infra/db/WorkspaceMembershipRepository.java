package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.workspace.WorkspaceMembership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMembershipRepository extends JpaRepository<WorkspaceMembership, UUID> {
  Optional<WorkspaceMembership> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  List<WorkspaceMembership> findByUserId(UUID userId);

  List<WorkspaceMembership> findByWorkspaceId(UUID workspaceId);

  boolean existsByWorkspaceIdAndUserIdAndRoleIn(UUID workspaceId, UUID userId, List<Role> roles);
}
