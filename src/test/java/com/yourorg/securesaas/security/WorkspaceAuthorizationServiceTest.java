package com.yourorg.securesaas.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yourorg.securesaas.app.security.WorkspaceAuthorizationService;
import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.workspace.WorkspaceMembership;
import com.yourorg.securesaas.infra.db.WorkspaceMembershipRepository;
import com.yourorg.securesaas.infra.security.AuthenticatedPrincipal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class WorkspaceAuthorizationServiceTest {

  @AfterEach
  void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void adminCanWriteProjects() {
    WorkspaceMembershipRepository membershipRepository = mock(WorkspaceMembershipRepository.class);
    WorkspaceAuthorizationService service = new WorkspaceAuthorizationService(membershipRepository);
    UUID userId = UUID.randomUUID();
    UUID workspaceId = UUID.randomUUID();

    when(membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId))
        .thenReturn(
            Optional.of(new WorkspaceMembership(UUID.randomUUID(), workspaceId, userId, Role.ADMIN)));

    AuthenticatedPrincipal principal = new AuthenticatedPrincipal(userId, "user@example.com", null, false);
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, "token"));

    assertThat(service.canAccessWorkspace(workspaceId, "PROJECT_WRITE")).isTrue();
  }

  @Test
  void apiKeyCannotWriteInvitations() {
    WorkspaceMembershipRepository membershipRepository = mock(WorkspaceMembershipRepository.class);
    WorkspaceAuthorizationService service = new WorkspaceAuthorizationService(membershipRepository);
    UUID workspaceId = UUID.randomUUID();

    AuthenticatedPrincipal principal = new AuthenticatedPrincipal(null, null, workspaceId, true);
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, "token"));

    assertThat(service.canAccessWorkspace(workspaceId, "INVITATION_WRITE")).isFalse();
  }
}
