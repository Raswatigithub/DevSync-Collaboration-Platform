package com.devsync.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
  Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);
  long countByAuthorId(UUID authorId);

  @Query(value = "select * from questions q where :tag = any(q.tags) order by q.created_at desc", nativeQuery = true)
  Page<Question> findByTag(@Param("tag") String tag, Pageable pageable);
}
