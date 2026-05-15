package com.devsync.reviews;

import com.devsync.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "review_comments")
public class ReviewComment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "review_id")
  private CodeReview review;

  @Column(name = "file_path", nullable = false)
  private String filePath;

  @Column(name = "line_number", nullable = false)
  private int lineNumber;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @Column(nullable = false)
  private String content;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}
