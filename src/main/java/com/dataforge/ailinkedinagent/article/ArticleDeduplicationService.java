package com.dataforge.ailinkedinagent.article;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class ArticleDeduplicationService {
    public String hash(String content) {
        try {
            String normalized = content == null ? "" : content.replaceAll("\\s+", " ").trim().toLowerCase();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash content", e);
        }
    }
}
