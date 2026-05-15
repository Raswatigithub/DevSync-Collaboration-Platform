package com.devsync.reviews;

import com.devsync.projects.ProjectRepository;
import com.devsync.projects.ProjectService;
import com.devsync.users.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {
  private final CodeReviewRepository reviews;
  private final ReviewCommentRepository comments;
  private final ProjectRepository projects;
  private final ProjectService projectService;

  @PostMapping("/api/projects/{projectId}/reviews")
  ReviewDtos.ReviewResponse create(@AuthenticationPrincipal User user, @PathVariable UUID projectId, @Valid @RequestBody ReviewDtos.CreateReviewRequest request) {
    projectService.requireMember(projectId, user);
    CodeReview review = new CodeReview();
    review.setProject(projects.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found")));
    review.setAuthor(user);
    review.setTitle(request.title());
    return ReviewDtos.ReviewResponse.from(reviews.save(review), List.of());
  }

  @GetMapping("/api/projects/{projectId}/reviews")
  List<ReviewDtos.ReviewResponse> list(@AuthenticationPrincipal User user, @PathVariable UUID projectId) {
    projectService.requireMember(projectId, user);
    return reviews.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
      .map(review -> ReviewDtos.ReviewResponse.from(review, comments.findByReviewIdOrderByCreatedAtAsc(review.getId()).stream().map(ReviewDtos.CommentResponse::from).toList()))
      .toList();
  }

  @PostMapping("/api/reviews/{reviewId}/comments")
  ReviewDtos.CommentResponse comment(@AuthenticationPrincipal User user, @PathVariable UUID reviewId, @Valid @RequestBody ReviewDtos.AddCommentRequest request) {
    CodeReview review = reviews.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("Review not found"));
    projectService.requireMember(review.getProject().getId(), user);
    ReviewComment comment = new ReviewComment();
    comment.setReview(review);
    comment.setAuthor(user);
    comment.setFilePath(request.filePath());
    comment.setLineNumber(request.lineNumber());
    comment.setContent(request.content());
    return ReviewDtos.CommentResponse.from(comments.save(comment));
  }

  @PatchMapping("/api/reviews/{reviewId}/status")
  ReviewDtos.ReviewResponse status(@AuthenticationPrincipal User user, @PathVariable UUID reviewId, @Valid @RequestBody ReviewDtos.UpdateStatusRequest request) {
    CodeReview review = reviews.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("Review not found"));
    projectService.requireMember(review.getProject().getId(), user);
    review.setStatus(request.status());
    CodeReview saved = reviews.save(review);
    return ReviewDtos.ReviewResponse.from(saved, comments.findByReviewIdOrderByCreatedAtAsc(reviewId).stream().map(ReviewDtos.CommentResponse::from).toList());
  }
}
