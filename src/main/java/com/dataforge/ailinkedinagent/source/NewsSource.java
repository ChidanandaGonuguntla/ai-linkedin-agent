package com.dataforge.ailinkedinagent.source;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ai_news_source")
public class NewsSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String sourceType;
    @Column(columnDefinition = "TEXT")
    private String url;
    private BigDecimal credibilityScore = BigDecimal.valueOf(5.0);
    private Boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
