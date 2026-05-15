package com.devsync.projects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ProjectDtos {
  public record CreateProjectRequest(@NotBlank String name, String description) {}
  public record InviteRequest(@NotNull UUID userId) {}
  public record ProjectResponse(UUID id, String name, String description, UUID ownerId, ProjectRole role) {
    public static ProjectResponse from(Project project, ProjectRole role) {
      return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getOwner().getId(), role);
    }
  }
  public record MemberResponse(UUID userId, String username, ProjectRole role) {}
}
