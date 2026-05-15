package com.devsync.ai;

import jakarta.validation.constraints.NotBlank;

public class AiDtos {
  public record AiSuggestionRequest(@NotBlank String language, @NotBlank String code, @NotBlank String task) {}
  public record AiSuggestionResponse(String result) {}
}
