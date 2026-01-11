package com.yourorg.securesaas.app.workspaces;

import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.workspace.Workspace;
import com.yourorg.securesaas.domain.workspace.WorkspaceMembership;
import com.yourorg.securesaas.infra.db.WorkspaceMembershipRepository;
import com.yourorg.securesaas.infra.db.WorkspaceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceService {

  private final WorkspaceRepository workspaceRepository;
  private final WorkspaceMembershipRepository membershipRepository;

  public WorkspaceService(
      WorkspaceRepository workspaceRepository, WorkspaceMembershipRepository membershipRepository) {
    this.workspaceRepository = workspaceRepository;
    this.membershipRepository = membershipRepository;
  }

  @Transactional
  public Workspace createWorkspace(UUID userId, String name) {
    Workspace workspace = new Workspace(UUID.randomUUID(), name, userId);
    workspaceRepository.save(workspace);
    membershipRepository.save(
        new WorkspaceMembership(UUID.randomUUID(), workspace.getId(), userId, Role.OWNER));
    return workspace;
  }

  public List<Workspace> listWorkspaces(UUID userId) {
    return membershipRepository.findByUserId(userId).stream()
        .map(membership -> workspaceRepository.findById(membership.getWorkspaceId()).orElse(null))
        .filter(workspace -> workspace != null)
        .toList();
  }
}
