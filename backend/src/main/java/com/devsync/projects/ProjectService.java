package com.devsync.projects;

import com.devsync.users.User;
import com.devsync.users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepository projects;
  private final ProjectMemberRepository members;
  private final UserRepository users;

  @Transactional
  public ProjectDtos.ProjectResponse create(User user, ProjectDtos.CreateProjectRequest request) {
    Project project = new Project();
    project.setName(request.name());
    project.setDescription(request.description());
    project.setOwner(user);
    Project saved = projects.save(project);

    ProjectMember owner = new ProjectMember();
    owner.setProject(saved);
    owner.setUser(user);
    owner.setRole(ProjectRole.OWNER);
    members.save(owner);
    return ProjectDtos.ProjectResponse.from(saved, ProjectRole.OWNER);
  }

  public List<ProjectDtos.ProjectResponse> list(User user) {
    return members.findByUser(user).stream()
      .map(member -> ProjectDtos.ProjectResponse.from(member.getProject(), member.getRole()))
      .toList();
  }

  public ProjectDtos.ProjectResponse get(User user, UUID projectId) {
    ProjectMember member = requireMember(projectId, user);
    return ProjectDtos.ProjectResponse.from(member.getProject(), member.getRole());
  }

  @Transactional
  public void invite(User actor, UUID projectId, UUID userId) {
    ProjectMember actorMember = requireMember(projectId, actor);
    if (actorMember.getRole() != ProjectRole.OWNER) {
      throw new AccessDeniedException("Only owners can invite members");
    }
    if (members.existsByProjectIdAndUserId(projectId, userId)) {
      return;
    }
    User invited = users.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    Project project = projects.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    ProjectMember member = new ProjectMember();
    member.setProject(project);
    member.setUser(invited);
    member.setRole(ProjectRole.MEMBER);
    members.save(member);
  }

  @Transactional
  public void join(User user, UUID projectId) {
    if (members.existsByProjectIdAndUserId(projectId, user.getId())) {
      return;
    }
    Project project = projects.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    ProjectMember member = new ProjectMember();
    member.setProject(project);
    member.setUser(user);
    member.setRole(ProjectRole.MEMBER);
    members.save(member);
  }

  public List<ProjectDtos.MemberResponse> members(UUID projectId, User user) {
    requireMember(projectId, user);
    return members.findByProjectId(projectId).stream()
      .map(member -> new ProjectDtos.MemberResponse(member.getUser().getId(), member.getUser().getUsername(), member.getRole()))
      .toList();
  }

  public ProjectMember requireMember(UUID projectId, User user) {
    return members.findByProjectIdAndUserId(projectId, user.getId())
      .orElseThrow(() -> new AccessDeniedException("Project membership required"));
  }
}
