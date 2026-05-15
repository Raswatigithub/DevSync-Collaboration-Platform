package com.devsync.forum;

import com.devsync.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "questions")
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @Column(nullable = false, length = 180)
  private String title;

  @Column(nullable = false)
  private String body;

  @Column(columnDefinition = "text[]")
  @JdbcTypeCode(SqlTypes.ARRAY)
  private List<String> tags = new ArrayList<>();

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}
