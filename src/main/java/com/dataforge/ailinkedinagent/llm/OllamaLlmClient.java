package com.dataforge.ailinkedinagent.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "agent.llm.provider", havingValue = "ollama", matchIfMissing = true)
public class OllamaLlmClient implements LlmClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${agent.llm.ollama-url}")
    private String ollamaUrl;

    @Value("${agent.llm.ollama-model}")
    private String model;

    @Override
    public String generate(String prompt) {
        Map<String, Object> request = Map.of("model", model, "prompt", prompt, "stream", false);
        Map<?, ?> response = webClientBuilder.baseUrl(ollamaUrl).build()
                .post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        if (response == null || response.get("response") == null) {
            throw new IllegalStateException("Empty response from Ollama");
        }
        return response.get("response").toString().trim();
    }
}
