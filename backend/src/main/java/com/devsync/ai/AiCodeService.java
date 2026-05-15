package com.devsync.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiCodeService {
  private final RestClient.Builder restClientBuilder;

  @Value("${app.openai.api-key}")
  private String apiKey;

  @Value("${app.openai.model}")
  private String model;

  public AiDtos.AiSuggestionResponse complete(AiDtos.AiSuggestionRequest request) {
    if (apiKey == null || apiKey.isBlank()) {
      return new AiDtos.AiSuggestionResponse("OPENAI_API_KEY is not configured. Add it to enable AI suggestions.");
    }

    String prompt = """
      You are a senior software engineer reviewing code for DevSync.
      Language: %s
      Task: %s

      Return concise, actionable guidance and improved code where useful.

      Code:
      %s
      """.formatted(request.language(), request.task(), request.code());

    Map<String, Object> body = Map.of(
      "model", model,
      "messages", List.of(
        Map.of("role", "system", "content", "You help developers improve, explain, debug, and optimize code."),
        Map.of("role", "user", "content", prompt)
      )
    );

    Map<?, ?> response = restClientBuilder
      .baseUrl("https://api.openai.com/v1")
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
      .build()
      .post()
      .uri("/chat/completions")
      .body(body)
      .retrieve()
      .body(Map.class);

    Object content = ((Map<?, ?>) ((Map<?, ?>) ((List<?>) response.get("choices")).get(0)).get("message")).get("content");
    return new AiDtos.AiSuggestionResponse(String.valueOf(content));
  }
}
