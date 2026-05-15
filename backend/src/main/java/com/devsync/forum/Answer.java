package com.devsync.forum;

import com.devsync.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "question_id")
  private Question question;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @Column(nullable = false)
  private String body;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}
