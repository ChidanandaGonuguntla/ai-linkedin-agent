package com.dataforge.ailinkedinagent.scheduler;

import com.dataforge.ailinkedinagent.article.NewsArticleRepository;
import com.dataforge.ailinkedinagent.article.ArticleCrawlerService;
import com.dataforge.ailinkedinagent.audit.AgentAuditService;
import com.dataforge.ailinkedinagent.draft.LinkedinDraftService;
import com.dataforge.ailinkedinagent.llm.SummaryGenerationService;
import com.dataforge.ailinkedinagent.scoring.ArticleRankingService;
import com.dataforge.ailinkedinagent.scoring.ArticleScoreRepository;
import com.dataforge.ailinkedinagent.source.NewsSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiLinkedinAgentOrchestrator {
    private final NewsSourceRepository sourceRepository;
    private final ArticleCrawlerService crawlerService;
    private final ArticleRankingService rankingService;
    private final ArticleScoreRepository scoreRepository;
    private final NewsArticleRepository articleRepository;
    private final SummaryGenerationService summaryGenerationService;
    private final LinkedinDraftService draftService;
    private final AgentAuditService auditService;

    @Value("${agent.crawler.max-articles-per-run:40}")
    private int maxArticlesPerRun;

    public AgentRunResult run() {
        int sourcesProcessed = 0;
        int articlesSaved = 0;
        int draftsGenerated = 0;
        try {
            var sources = sourceRepository.findByEnabledTrueOrderByCredibilityScoreDesc();
            for (var source : sources) {
                int saved = crawlerService.crawlRssSource(source.getId(), source.getUrl(), 10);
                articlesSaved += saved;
                sourcesProcessed++;
            }
            var scores = rankingService.scoreCandidates(maxArticlesPerRun);
            var topScores = scoreRepository.topScores(PageRequest.of(0, 5));
            for (var score : topScores) {
                var article = articleRepository.findById(score.getArticleId()).orElse(null);
                if (article == null || "DRAFTED".equals(article.getStatus())) continue;
                try {
                    var summary = summaryGenerationService.summarize(article);
                    draftService.generateDraft(article, summary);
                    draftsGenerated++;
                } catch (Exception e) {
                    log.warn("Failed to generate draft for articleId={}", article.getId(), e);
                    auditService.log("GENERATE_DRAFT", "ARTICLE", article.getId(), "FAILED", e.getMessage());
                }
            }
            auditService.log("AGENT_RUN", "AGENT", null, "SUCCESS", "Run completed");
            return new AgentRunResult(sourcesProcessed, articlesSaved, scores.size(), draftsGenerated, "SUCCESS");
        } catch (Exception e) {
            log.error("Agent run failed", e);
            auditService.log("AGENT_RUN", "AGENT", null, "FAILED", e.getMessage());
            return new AgentRunResult(sourcesProcessed, articlesSaved, 0, draftsGenerated, "FAILED: " + e.getMessage());
        }
    }
}
