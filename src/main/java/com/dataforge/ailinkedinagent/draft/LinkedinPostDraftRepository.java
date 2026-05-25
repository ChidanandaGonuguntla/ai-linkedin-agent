package com.dataforge.ailinkedinagent.draft;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LinkedinPostDraftRepository extends JpaRepository<LinkedinPostDraft, Long> {
    List<LinkedinPostDraft> findByStatusOrderByCreatedAtDesc(String status);
    boolean existsByArticleIdAndStatusIn(Long articleId, List<String> statuses);
}
