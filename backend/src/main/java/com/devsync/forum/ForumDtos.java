package com.devsync.forum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ForumDtos {
  public record CreateQuestionRequest(@NotBlank String title, @NotBlank String body, List<String> tags) {}
  public record CreateAnswerRequest(@NotBlank String body) {}
  public record VoteRequest(@NotBlank String targetType, @NotNull UUID targetId, int value) {}
  public record AnswerResponse(UUID id, UUID authorId, String authorName, String body, Instant createdAt) {
    public static AnswerResponse from(Answer answer) {
      return new AnswerResponse(answer.getId(), answer.getAuthor().getId(), answer.getAuthor().getUsername(), answer.getBody(), answer.getCreatedAt());
    }
  }
  public record QuestionResponse(UUID id, UUID authorId, String authorName, String title, String body, List<String> tags, Instant createdAt, List<AnswerResponse> answers) {
    public static QuestionResponse from(Question question, List<AnswerResponse> answers) {
      return new QuestionResponse(question.getId(), question.getAuthor().getId(), question.getAuthor().getUsername(), question.getTitle(), question.getBody(), question.getTags(), question.getCreatedAt(), answers);
    }
  }
}
