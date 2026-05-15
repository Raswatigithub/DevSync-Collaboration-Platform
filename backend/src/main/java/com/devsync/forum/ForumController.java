package com.devsync.forum;

import com.devsync.users.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ForumController {
  private final QuestionRepository questions;
  private final AnswerRepository answers;
  private final VoteRepository votes;

  @PostMapping("/questions")
  ForumDtos.QuestionResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody ForumDtos.CreateQuestionRequest request) {
    Question question = new Question();
    question.setAuthor(user);
    question.setTitle(request.title());
    question.setBody(request.body());
    question.setTags(request.tags() == null ? List.of() : request.tags());
    return ForumDtos.QuestionResponse.from(questions.save(question), List.of());
  }

  @GetMapping("/questions")
  Page<ForumDtos.QuestionResponse> list(@RequestParam(required = false) String tag, @RequestParam(defaultValue = "0") int page) {
    PageRequest pageable = PageRequest.of(page, 20);
    Page<Question> result = tag == null || tag.isBlank() ? questions.findAllByOrderByCreatedAtDesc(pageable) : questions.findByTag(tag, pageable);
    return result.map(question -> ForumDtos.QuestionResponse.from(question, List.of()));
  }

  @GetMapping("/questions/{id}")
  ForumDtos.QuestionResponse get(@PathVariable UUID id) {
    Question question = questions.findById(id).orElseThrow(() -> new EntityNotFoundException("Question not found"));
    return ForumDtos.QuestionResponse.from(question, answers.findByQuestionIdOrderByCreatedAtAsc(id).stream().map(ForumDtos.AnswerResponse::from).toList());
  }

  @PostMapping("/questions/{id}/answers")
  ForumDtos.AnswerResponse answer(@AuthenticationPrincipal User user, @PathVariable UUID id, @Valid @RequestBody ForumDtos.CreateAnswerRequest request) {
    Question question = questions.findById(id).orElseThrow(() -> new EntityNotFoundException("Question not found"));
    Answer answer = new Answer();
    answer.setQuestion(question);
    answer.setAuthor(user);
    answer.setBody(request.body());
    return ForumDtos.AnswerResponse.from(answers.save(answer));
  }

  @PostMapping("/votes")
  void vote(@AuthenticationPrincipal User user, @Valid @RequestBody ForumDtos.VoteRequest request) {
    if (request.value() != 1 && request.value() != -1) {
      throw new IllegalArgumentException("Vote value must be 1 or -1");
    }
    Vote vote = votes.findByUserIdAndTargetTypeAndTargetId(user.getId(), request.targetType(), request.targetId()).orElseGet(Vote::new);
    vote.setUser(user);
    vote.setTargetType(request.targetType());
    vote.setTargetId(request.targetId());
    vote.setValue(request.value());
    votes.save(vote);
  }
}
