package com.dataforge.ailinkedinagent.scoring;

import com.dataforge.ailinkedinagent.article.NewsArticle;
import com.dataforge.ailinkedinagent.article.NewsArticleRepository;
import com.dataforge.ailinkedinagent.source.NewsSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleRankingService {
    private final ArticleScoreRepository scoreRepository;
    private final NewsArticleRepository articleRepository;
    private final NewsSourceRepository sourceRepository;

    private static final List<String> AI_TERMS = List.of(
            "ai", "artificial intelligence", "llm", "model", "agent", "agents", "rag", "openai", "anthropic",
            "deepmind", "nvidia", "inference", "embedding", "vector", "multimodal", "mcp", "model context protocol"
    );

    private static final List<String> ENTERPRISE_TERMS = List.of(
            "enterprise", "workflow", "automation", "governance", "security", "audit", "data", "database",
            "semantic layer", "java", "spring", "postgres", "trino", "kafka", "healthcare", "fhir", "production"
    );

    @Transactional
    public ArticleScore scoreArticle(NewsArticle article) {
        double credibility = sourceRepository.findById(article.getSourceId())
                .map(s -> s.getCredibilityScore().doubleValue())
                .orElse(5.0);
        double freshness = freshness(article);
        double ai = keywordScore(article, AI_TERMS);
        double enterprise = keywordScore(article, ENTERPRISE_TERMS);
        double finalScore = freshness * 0.25 + credibility * 0.20 + ai * 0.30 + enterprise * 0.25;

        ArticleScore score = new ArticleScore();
        score.setArticleId(article.getId());
        score.setFreshnessScore(bd(freshness));
        score.setCredibilityScore(bd(credibility));
        score.setAiRelevanceScore(bd(ai));
        score.setEnterpriseRelevanceScore(bd(enterprise));
        score.setFinalScore(bd(finalScore));
        score.setScoringReason("Weighted score = freshness 25%, credibility 20%, AI relevance 30%, enterprise relevance 25%.");
        article.setStatus("SCORED");
        articleRepository.save(article);
        return scoreRepository.save(score);
    }

    public List<ArticleScore> scoreCandidates(int limit) {
        return articleRepository.findCandidates(PageRequest.of(0, limit)).stream()
                .map(this::scoreArticle)
                .toList();
    }

    private double freshness(NewsArticle article) {
        LocalDateTime date = article.getPublishedAt() == null ? article.getCrawledAt() : article.getPublishedAt();
        long hours = Duration.between(date, LocalDateTime.now()).toHours();
        if (hours <= 24) return 10.0;
        if (hours <= 72) return 8.0;
        if (hours <= 168) return 6.5;
        if (hours <= 720) return 4.0;
        return 2.0;
    }

    private double keywordScore(NewsArticle article, List<String> terms) {
        String text = (article.getTitle() + "\n" + article.getRawContent()).toLowerCase();
        long matches = terms.stream().filter(text::contains).count();
        return Math.min(10.0, matches * 1.25);
    }

    private BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
