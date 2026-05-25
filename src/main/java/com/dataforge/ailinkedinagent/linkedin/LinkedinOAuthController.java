package com.dataforge.ailinkedinagent.linkedin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/linkedin/oauth")
@RequiredArgsConstructor
public class LinkedinOAuthController {
    private final WebClient.Builder webClientBuilder;
    private final LinkedinTokenRepository tokenRepository;

    @Value("${linkedin.client-id:}")
    private String clientId;
    @Value("${linkedin.client-secret:}")
    private String clientSecret;
    @Value("${linkedin.redirect-uri:}")
    private String redirectUri;

    @GetMapping("/authorize-url")
    public Map<String, String> authorizeUrl() {
        String url = UriComponentsBuilder.fromUriString("https://www.linkedin.com/oauth/v2/authorization")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid profile w_member_social")
                .build().toUriString();
        return Map.of("url", url);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code) {
        Map<?, ?> tokenResponse = webClientBuilder.build()
                .post()
                .uri("https://www.linkedin.com/oauth/v2/accessToken")
                .bodyValue("grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri
                        + "&client_id=" + clientId + "&client_secret=" + clientSecret)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (tokenResponse == null || tokenResponse.get("access_token") == null) {
            return ResponseEntity.badRequest().body("LinkedIn token exchange failed");
        }
        String accessToken = tokenResponse.get("access_token").toString();
        Integer expiresIn = tokenResponse.get("expires_in") == null ? 3600 : Integer.parseInt(tokenResponse.get("expires_in").toString());

        Map<?, ?> userInfo = webClientBuilder.build()
                .get()
                .uri("https://api.linkedin.com/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        String sub = userInfo == null || userInfo.get("sub") == null ? null : userInfo.get("sub").toString();

        LinkedinToken token = new LinkedinToken();
        token.setAccessToken(accessToken);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
        token.setMemberUrn(sub == null ? null : "urn:li:person:" + sub);
        tokenRepository.save(token);
        return ResponseEntity.ok("LinkedIn connected. You can close this window.");
    }
}
