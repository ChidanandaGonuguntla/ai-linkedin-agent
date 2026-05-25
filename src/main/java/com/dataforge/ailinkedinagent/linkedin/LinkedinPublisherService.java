package com.dataforge.ailinkedinagent.linkedin;

import com.dataforge.ailinkedinagent.draft.LinkedinPostDraft;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LinkedinPublisherService {
    private final WebClient.Builder webClientBuilder;
    private final LinkedinTokenRepository tokenRepository;

    @Value("${linkedin.api-version:202605}")
    private String linkedinApiVersion;

    public String publish(LinkedinPostDraft draft) {
        LinkedinToken token = tokenRepository.findTopByProviderOrderByUpdatedAtDesc("LINKEDIN")
                .orElseThrow(() -> new IllegalStateException("LinkedIn token not connected. Configure OAuth/token before publishing."));
        if (token.getMemberUrn() == null || token.getMemberUrn().isBlank()) {
            throw new IllegalStateException("LinkedIn member URN is missing. Complete OAuth profile lookup first.");
        }
        Map<String, Object> payload = Map.of(
                "author", token.getMemberUrn(),
                "commentary", draft.getPostBody(),
                "visibility", "PUBLIC",
                "distribution", Map.of(
                        "feedDistribution", "MAIN_FEED",
                        "targetEntities", List.of(),
                        "thirdPartyDistributionChannels", List.of()
                ),
                "lifecycleState", "PUBLISHED",
                "isReshareDisabledByAuthor", false
        );
        String response = webClientBuilder.build()
                .post()
                .uri("https://api.linkedin.com/rest/posts")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .header("LinkedIn-Version", linkedinApiVersion)
                .header("X-Restli-Protocol-Version", "2.0.0")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response == null || response.isBlank() ? "PUBLISHED" : response;
    }
}
