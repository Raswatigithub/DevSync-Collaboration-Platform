package com.devsync.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class ChatDtos {
  public record SendMessageRequest(@NotNull UUID projectId, @NotBlank String content) {}
  public record ChatMessageResponse(UUID id, UUID projectId, UUID senderId, String senderName, String content, Instant createdAt) {
    public static ChatMessageResponse from(ChatMessage message) {
      return new ChatMessageResponse(
        message.getId(),
        message.getProject().getId(),
        message.getSender().getId(),
        message.getSender().getUsername(),
        message.getContent(),
        message.getCreatedAt()
      );
    }
  }
}
