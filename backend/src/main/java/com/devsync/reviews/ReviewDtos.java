package com.devsync.reviews;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReviewDtos {
  public record CreateReviewRequest(@NotBlank String title) {}
  public record AddCommentRequest(@NotBlank String filePath, @Min(1) int lineNumber, @NotBlank String content) {}
  public record UpdateStatusRequest(@NotNull ReviewStatus status) {}
  public record CommentResponse(UUID id, String filePath, int lineNumber, UUID authorId, String content, Instant createdAt) {
    public static CommentResponse from(ReviewComment comment) {
      return new CommentResponse(comment.getId(), comment.getFilePath(), comment.getLineNumber(), comment.getAuthor().getId(), comment.getContent(), comment.getCreatedAt());
    }
  }
  public record ReviewResponse(UUID id, UUID projectId, UUID authorId, String title, ReviewStatus status, Instant createdAt, List<CommentResponse> comments) {
    public static ReviewResponse from(CodeReview review, List<CommentResponse> comments) {
      return new ReviewResponse(review.getId(), review.getProject().getId(), review.getAuthor().getId(), review.getTitle(), review.getStatus(), review.getCreatedAt(), comments);
    }
  }
}
