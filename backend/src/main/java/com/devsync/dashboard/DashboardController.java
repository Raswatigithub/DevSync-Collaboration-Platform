package com.devsync.dashboard;

import com.devsync.forum.AnswerRepository;
import com.devsync.forum.QuestionRepository;
import com.devsync.projects.ProjectMemberRepository;
import com.devsync.reviews.CodeReviewRepository;
import com.devsync.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
  private final ProjectMemberRepository projectMembers;
  private final QuestionRepository questions;
  private final AnswerRepository answers;
  private final CodeReviewRepository reviews;

  @GetMapping("/summary")
  DashboardSummary summary(@AuthenticationPrincipal User user) {
    return new DashboardSummary(
      projectMembers.countByUser(user),
      questions.countByAuthorId(user.getId()),
      answers.countByAuthorId(user.getId()),
      reviews.countByAuthorId(user.getId())
    );
  }

  public record DashboardSummary(long projectsJoined, long questionsPosted, long answersPosted, long reviewsCreated) {
  }
}
