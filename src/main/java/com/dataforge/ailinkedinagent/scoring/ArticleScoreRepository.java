package com.dataforge.ailinkedinagent.scoring;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleScoreRepository extends JpaRepository<ArticleScore, Long> {
    Optional<ArticleScore> findTopByArticleIdOrderByCreatedAtDesc(Long articleId);

    @Query("select s from ArticleScore s order by s.finalScore desc, s.createdAt desc")
    List<ArticleScore> topScores(Pageable pageable);
}
