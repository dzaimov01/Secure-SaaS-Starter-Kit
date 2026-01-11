package com.yourorg.securesaas.api.projects;

import com.yourorg.securesaas.app.projects.ProjectService;
import com.yourorg.securesaas.domain.project.Project;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'PROJECT_READ')")
  public List<ProjectResponse> list(@RequestParam("workspaceId") UUID workspaceId) {
    return projectService.listProjects(workspaceId).stream().map(this::toResponse).toList();
  }

  @PostMapping
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'PROJECT_WRITE')")
  public ProjectResponse create(
      @RequestParam("workspaceId") UUID workspaceId, @Valid @RequestBody ProjectRequest request) {
    Project project = projectService.createProject(workspaceId, request.name(), request.description());
    return toResponse(project);
  }

  @PutMapping("/{projectId}")
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'PROJECT_WRITE')")
  public ProjectResponse update(
      @RequestParam("workspaceId") UUID workspaceId,
      @PathVariable UUID projectId,
      @Valid @RequestBody ProjectRequest request) {
    Project project = projectService.updateProject(workspaceId, projectId, request.name(), request.description());
    return toResponse(project);
  }

  @DeleteMapping("/{projectId}")
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'PROJECT_WRITE')")
  public void delete(
      @RequestParam("workspaceId") UUID workspaceId, @PathVariable UUID projectId) {
    projectService.deleteProject(workspaceId, projectId);
  }

  private ProjectResponse toResponse(Project project) {
    return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
  }
}
