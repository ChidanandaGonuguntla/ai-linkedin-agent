package com.dataforge.ailinkedinagent.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "agent.llm.provider", havingValue = "openai")
public class OpenAiLlmClient implements LlmClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${agent.llm.openai-api-key}")
    private String apiKey;

    @Value("${agent.llm.openai-model}")
    private String model;

    @Override
    public String generate(String prompt) {
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.4
        );
        Map<?, ?> response = webClientBuilder.baseUrl("https://api.openai.com").build()
                .post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        var choices = (List<?>) response.get("choices");
        var first = (Map<?, ?>) choices.getFirst();
        var message = (Map<?, ?>) first.get("message");
        return message.get("content").toString().trim();
    }
}
