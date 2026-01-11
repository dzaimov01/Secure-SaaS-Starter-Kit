package com.yourorg.securesaas.api.workspaces;

import com.yourorg.securesaas.app.workspaces.WorkspaceService;
import com.yourorg.securesaas.domain.workspace.Workspace;
import com.yourorg.securesaas.infra.security.AuthenticatedPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

  private final WorkspaceService workspaceService;

  public WorkspaceController(WorkspaceService workspaceService) {
    this.workspaceService = workspaceService;
  }

  @GetMapping
  public List<WorkspaceResponse> list() {
    AuthenticatedPrincipal principal = currentPrincipal();
    return workspaceService.listWorkspaces(principal.getUserId()).stream()
        .map(workspace -> new WorkspaceResponse(workspace.getId(), workspace.getName()))
        .toList();
  }

  @PostMapping
  public WorkspaceResponse create(@Valid @RequestBody WorkspaceRequest request) {
    AuthenticatedPrincipal principal = currentPrincipal();
    Workspace workspace = workspaceService.createWorkspace(principal.getUserId(), request.name());
    return new WorkspaceResponse(workspace.getId(), workspace.getName());
  }

  private AuthenticatedPrincipal currentPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedPrincipal)) {
      throw new IllegalStateException("unauthenticated");
    }
    return (AuthenticatedPrincipal) authentication.getPrincipal();
  }
}
