package com.dataforge.ailinkedinagent.llm;

import com.dataforge.ailinkedinagent.article.NewsArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SummaryGenerationService {
    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;
    private final ArticleSummaryRepository repository;

    @Value("${agent.llm.provider:ollama}")
    private String provider;

    @Transactional
    public ArticleSummary summarize(NewsArticle article) {
        String generated = llmClient.generate(promptBuilder.summaryPrompt(article));
        ArticleSummary summary = new ArticleSummary();
        summary.setArticleId(article.getId());
        summary.setShortSummary(firstN(generated, 900));
        summary.setDetailedSummary(generated);
        summary.setWhyItMatters(extractWhyItMatters(generated));
        summary.setGeneratedBy(provider);
        return repository.save(summary);
    }

    private String firstN(String text, int limit) {
        if (text == null) return "";
        return text.length() <= limit ? text : text.substring(0, limit);
    }

    private String extractWhyItMatters(String generated) {
        if (generated == null) return "";
        int idx = generated.toLowerCase().indexOf("why it matters");
        return idx >= 0 ? generated.substring(idx) : generated;
    }
}
