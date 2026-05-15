package com.devsync.editor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class EditorDtos {
  public record UpsertFileRequest(@NotBlank String path, String language, String content) {}
  public record EditorChangeRequest(@NotNull UUID projectId, @NotNull UUID fileId, String content, long version) {}
  public record CodeFileResponse(UUID id, UUID projectId, String path, String language, String content, Instant updatedAt) {
    public static CodeFileResponse from(CodeFile file) {
      return new CodeFileResponse(file.getId(), file.getProject().getId(), file.getPath(), file.getLanguage(), file.getContent(), file.getUpdatedAt());
    }
  }
}
