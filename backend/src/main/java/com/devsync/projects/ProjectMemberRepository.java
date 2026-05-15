package com.devsync.projects;

import com.devsync.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
  boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
  Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);
  List<ProjectMember> findByUser(User user);
  List<ProjectMember> findByProjectId(UUID projectId);
  long countByUser(User user);
}
