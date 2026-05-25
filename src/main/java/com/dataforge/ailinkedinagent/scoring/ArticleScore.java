package com.dataforge.ailinkedinagent.scoring;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ai_article_score")
public class ArticleScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long articleId;
    private BigDecimal freshnessScore;
    private BigDecimal credibilityScore;
    private BigDecimal aiRelevanceScore;
    private BigDecimal enterpriseRelevanceScore;
    private BigDecimal finalScore;
    @Column(columnDefinition = "TEXT")
    private String scoringReason;
    private LocalDateTime createdAt = LocalDateTime.now();
}
