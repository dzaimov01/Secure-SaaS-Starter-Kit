package com.yourorg.securesaas.app.security;

import com.yourorg.securesaas.domain.security.Permission;
import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.workspace.WorkspaceMembership;
import com.yourorg.securesaas.infra.db.WorkspaceMembershipRepository;
import com.yourorg.securesaas.infra.security.AuthenticatedPrincipal;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workspaceAuth")
public class WorkspaceAuthorizationService {

  private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS =
      Map.of(
          Role.OWNER,
          EnumSet.allOf(Permission.class),
          Role.ADMIN,
          EnumSet.of(
              Permission.WORKSPACE_READ,
              Permission.WORKSPACE_WRITE,
              Permission.PROJECT_READ,
              Permission.PROJECT_WRITE,
              Permission.API_KEY_READ,
              Permission.API_KEY_WRITE,
              Permission.INVITATION_READ,
              Permission.INVITATION_WRITE),
          Role.MEMBER,
          EnumSet.of(Permission.WORKSPACE_READ, Permission.PROJECT_READ, Permission.INVITATION_READ),
          Role.API_KEY,
          EnumSet.of(Permission.PROJECT_READ, Permission.PROJECT_WRITE));

  private final WorkspaceMembershipRepository membershipRepository;

  public WorkspaceAuthorizationService(WorkspaceMembershipRepository membershipRepository) {
    this.membershipRepository = membershipRepository;
  }

  public boolean canAccessWorkspace(UUID workspaceId, String permissionName) {
    Permission permission = Permission.valueOf(permissionName);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedPrincipal)) {
      return false;
    }
    AuthenticatedPrincipal principal = (AuthenticatedPrincipal) authentication.getPrincipal();
    if (principal.isApiKey()) {
      if (principal.getWorkspaceId() == null || !principal.getWorkspaceId().equals(workspaceId)) {
        return false;
      }
      return ROLE_PERMISSIONS.getOrDefault(Role.API_KEY, Set.of()).contains(permission);
    }

    Optional<WorkspaceMembership> membership =
        membershipRepository.findByWorkspaceIdAndUserId(workspaceId, principal.getUserId());
    return membership
        .map(WorkspaceMembership::getRole)
        .map(role -> ROLE_PERMISSIONS.getOrDefault(role, Set.of()).contains(permission))
        .orElse(false);
  }
}
