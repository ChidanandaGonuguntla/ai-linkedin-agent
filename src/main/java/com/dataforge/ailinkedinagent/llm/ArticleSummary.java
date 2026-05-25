package com.dataforge.ailinkedinagent.llm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ai_article_summary")
public class ArticleSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long articleId;
    @Column(columnDefinition = "TEXT")
    private String shortSummary;
    @Column(columnDefinition = "TEXT")
    private String detailedSummary;
    @Column(columnDefinition = "TEXT")
    private String whyItMatters;
    private String generatedBy;
    private LocalDateTime createdAt = LocalDateTime.now();
}
