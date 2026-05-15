package com.devsync.editor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeFileRepository extends JpaRepository<CodeFile, UUID> {
  List<CodeFile> findByProjectId(UUID projectId);
  Optional<CodeFile> findByProjectIdAndPath(UUID projectId, String path);
}
