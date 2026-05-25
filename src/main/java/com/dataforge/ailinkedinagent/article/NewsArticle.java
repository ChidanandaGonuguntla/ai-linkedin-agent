package com.dataforge.ailinkedinagent.article;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ai_news_article")
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sourceId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;
    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String url;
    private String author;
    private LocalDateTime publishedAt;
    @Column(columnDefinition = "TEXT")
    private String rawContent;
    private String contentHash;
    private LocalDateTime crawledAt = LocalDateTime.now();
    private String status = "NEW";
}
