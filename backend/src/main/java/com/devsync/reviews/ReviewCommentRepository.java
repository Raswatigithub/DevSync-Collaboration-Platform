package com.devsync.reviews;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, UUID> {
  List<ReviewComment> findByReviewIdOrderByCreatedAtAsc(UUID reviewId);
}
