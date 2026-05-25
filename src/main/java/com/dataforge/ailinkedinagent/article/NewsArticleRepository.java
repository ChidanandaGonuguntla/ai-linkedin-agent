package com.dataforge.ailinkedinagent.article;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    boolean existsByUrl(String url);
    boolean existsByContentHash(String contentHash);
    List<NewsArticle> findByStatusOrderByPublishedAtDesc(String status, Pageable pageable);

    @Query("select a from NewsArticle a where a.status in ('NEW','SCORED') order by coalesce(a.publishedAt, a.crawledAt) desc")
    List<NewsArticle> findCandidates(Pageable pageable);
}
