package com.devsync.forum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
  List<Answer> findByQuestionIdOrderByCreatedAtAsc(UUID questionId);
  long countByAuthorId(UUID authorId);
}
