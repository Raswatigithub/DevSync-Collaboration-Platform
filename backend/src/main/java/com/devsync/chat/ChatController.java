package com.devsync.chat;

import com.devsync.projects.ProjectRepository;
import com.devsync.projects.ProjectService;
import com.devsync.users.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {
  private final ChatMessageRepository messages;
  private final ProjectRepository projects;
  private final ProjectService projectService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat.send")
  public void send(@Payload ChatDtos.SendMessageRequest request, @AuthenticationPrincipal User user) {
    ChatDtos.ChatMessageResponse response = persist(request, user);
    messagingTemplate.convertAndSend("/topic/projects/" + request.projectId() + "/chat", response);
  }

  @GetMapping("/api/projects/{projectId}/chat")
  @ResponseBody
  List<ChatDtos.ChatMessageResponse> history(@AuthenticationPrincipal User user, @PathVariable UUID projectId) {
    projectService.requireMember(projectId, user);
    return messages.findTop50ByProjectIdOrderByCreatedAtDesc(projectId).stream()
      .map(ChatDtos.ChatMessageResponse::from)
      .toList();
  }

  private ChatDtos.ChatMessageResponse persist(ChatDtos.SendMessageRequest request, User user) {
    projectService.requireMember(request.projectId(), user);
    ChatMessage message = new ChatMessage();
    message.setProject(projects.findById(request.projectId()).orElseThrow(() -> new EntityNotFoundException("Project not found")));
    message.setSender(user);
    message.setContent(request.content());
    return ChatDtos.ChatMessageResponse.from(messages.save(message));
  }
}
