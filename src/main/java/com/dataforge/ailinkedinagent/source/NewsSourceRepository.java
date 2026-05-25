package com.dataforge.ailinkedinagent.source;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsSourceRepository extends JpaRepository<NewsSource, Long> {
    List<NewsSource> findByEnabledTrueOrderByCredibilityScoreDesc();
}
