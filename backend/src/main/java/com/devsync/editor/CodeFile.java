package com.devsync.editor;

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
@Table(name = "code_files")
public class CodeFile {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "project_id")
  private Project project;

  @Column(nullable = false)
  private String path;

  private String language;
  private String content;

  @ManyToOne
  @JoinColumn(name = "updated_by")
  private User updatedBy;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();
}
