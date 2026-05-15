package com.devsync.projects;

import com.devsync.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
  private final ProjectService projectService;

  @PostMapping
  ProjectDtos.ProjectResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody ProjectDtos.CreateProjectRequest request) {
    return projectService.create(user, request);
  }

  @GetMapping
  List<ProjectDtos.ProjectResponse> list(@AuthenticationPrincipal User user) {
    return projectService.list(user);
  }

  @GetMapping("/{projectId}")
  ProjectDtos.ProjectResponse get(@AuthenticationPrincipal User user, @PathVariable UUID projectId) {
    return projectService.get(user, projectId);
  }

  @PostMapping("/{projectId}/invite")
  void invite(@AuthenticationPrincipal User user, @PathVariable UUID projectId, @Valid @RequestBody ProjectDtos.InviteRequest request) {
    projectService.invite(user, projectId, request.userId());
  }

  @PostMapping("/{projectId}/join")
  void join(@AuthenticationPrincipal User user, @PathVariable UUID projectId) {
    projectService.join(user, projectId);
  }

  @GetMapping("/{projectId}/members")
  List<ProjectDtos.MemberResponse> members(@AuthenticationPrincipal User user, @PathVariable UUID projectId) {
    return projectService.members(projectId, user);
  }
}
