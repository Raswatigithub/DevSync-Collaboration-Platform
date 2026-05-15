package com.devsync.ai;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
  private final AiCodeService aiCodeService;

  @PostMapping("/code-suggestion")
  AiDtos.AiSuggestionResponse suggest(@Valid @RequestBody AiDtos.AiSuggestionRequest request) {
    return aiCodeService.complete(request);
  }

  @PostMapping("/explain-code")
  AiDtos.AiSuggestionResponse explain(@Valid @RequestBody AiDtos.AiSuggestionRequest request) {
    return aiCodeService.complete(new AiDtos.AiSuggestionRequest(request.language(), request.code(), "Explain this code clearly"));
  }

  @PostMapping("/fix-bug")
  AiDtos.AiSuggestionResponse fix(@Valid @RequestBody AiDtos.AiSuggestionRequest request) {
    return aiCodeService.complete(new AiDtos.AiSuggestionRequest(request.language(), request.code(), "Find and fix bugs"));
  }
}
