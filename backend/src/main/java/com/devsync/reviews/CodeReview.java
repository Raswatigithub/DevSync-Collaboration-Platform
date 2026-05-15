package com.devsync.reviews;

import com.devsync.projects.Project;
import com.devsync.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "code_reviews")
public class CodeReview {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @Column(nullable = false, length = 160)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReviewStatus status = ReviewStatus.OPEN;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}
