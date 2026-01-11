package com.yourorg.securesaas.app.projects;

import com.yourorg.securesaas.domain.project.Project;
import com.yourorg.securesaas.infra.db.ProjectRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public List<Project> listProjects(UUID workspaceId) {
    return projectRepository.findByWorkspaceId(workspaceId);
  }

  @Transactional
  public Project createProject(UUID workspaceId, String name, String description) {
    Project project = new Project(UUID.randomUUID(), workspaceId, name, description);
    return projectRepository.save(project);
  }

  @Transactional
  public Project updateProject(UUID workspaceId, UUID projectId, String name, String description) {
    Project project =
        projectRepository
            .findByIdAndWorkspaceId(projectId, workspaceId)
            .orElseThrow();
    project.update(name, description);
    return projectRepository.save(project);
  }

  @Transactional
  public void deleteProject(UUID workspaceId, UUID projectId) {
    Project project =
        projectRepository
            .findByIdAndWorkspaceId(projectId, workspaceId)
            .orElseThrow();
    projectRepository.delete(project);
  }
}
