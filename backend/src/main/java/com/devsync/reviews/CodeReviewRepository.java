package com.devsync.reviews;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodeReviewRepository extends JpaRepository<CodeReview, UUID> {
  List<CodeReview> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
  long countByAuthorId(UUID authorId);
}
