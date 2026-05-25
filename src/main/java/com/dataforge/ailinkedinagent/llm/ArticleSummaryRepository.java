package com.dataforge.ailinkedinagent.llm;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArticleSummaryRepository extends JpaRepository<ArticleSummary, Long> {
    Optional<ArticleSummary> findTopByArticleIdOrderByCreatedAtDesc(Long articleId);
}
