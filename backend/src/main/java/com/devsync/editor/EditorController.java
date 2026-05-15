package com.devsync.editor;

import com.devsync.projects.ProjectRepository;
import com.devsync.projects.ProjectService;
import com.devsync.users.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EditorController {
  private final CodeFileRepository files;
  private final ProjectRepository projects;
  private final ProjectService projectService;
  private final SimpMessagingTemplate messagingTemplate;

  @GetMapping("/api/projects/{projectId}/files")
  List<EditorDtos.CodeFileResponse> list(@AuthenticationPrincipal User user, @PathVariable UUID projectId) {
    projectService.requireMember(projectId, user);
    return files.findByProjectId(projectId).stream().map(EditorDtos.CodeFileResponse::from).toList();
  }

  @PostMapping("/api/projects/{projectId}/files")
  EditorDtos.CodeFileResponse upsert(@AuthenticationPrincipal User user, @PathVariable UUID projectId, @Valid @RequestBody EditorDtos.UpsertFileRequest request) {
    projectService.requireMember(projectId, user);
    CodeFile file = files.findByProjectIdAndPath(projectId, request.path()).orElseGet(CodeFile::new);
    file.setProject(projects.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found")));
    file.setPath(request.path());
    file.setLanguage(request.language());
    file.setContent(request.content());
    file.setUpdatedBy(user);
    file.setUpdatedAt(Instant.now());
    return EditorDtos.CodeFileResponse.from(files.save(file));
  }

  @PatchMapping("/api/files/{fileId}")
  EditorDtos.CodeFileResponse save(@AuthenticationPrincipal User user, @PathVariable UUID fileId, @RequestBody EditorDtos.UpsertFileRequest request) {
    CodeFile file = files.findById(fileId).orElseThrow(() -> new EntityNotFoundException("File not found"));
    projectService.requireMember(file.getProject().getId(), user);
    file.setContent(request.content());
    file.setUpdatedBy(user);
    file.setUpdatedAt(Instant.now());
    return EditorDtos.CodeFileResponse.from(files.save(file));
  }

  @MessageMapping("/editor.change")
  public void change(@Payload EditorDtos.EditorChangeRequest request, @AuthenticationPrincipal User user) {
    projectService.requireMember(request.projectId(), user);
    messagingTemplate.convertAndSend("/topic/projects/" + request.projectId() + "/editor", request);
  }
}
