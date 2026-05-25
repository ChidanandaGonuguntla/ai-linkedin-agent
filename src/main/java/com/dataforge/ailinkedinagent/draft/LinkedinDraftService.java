package com.dataforge.ailinkedinagent.draft;

import com.dataforge.ailinkedinagent.article.NewsArticle;
import com.dataforge.ailinkedinagent.article.NewsArticleRepository;
import com.dataforge.ailinkedinagent.linkedin.LinkedinPublisherService;
import com.dataforge.ailinkedinagent.llm.ArticleSummary;
import com.dataforge.ailinkedinagent.llm.LlmClient;
import com.dataforge.ailinkedinagent.llm.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkedinDraftService {
    private final LinkedinPostDraftRepository draftRepository;
    private final NewsArticleRepository articleRepository;
    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;
    private final LinkedinPublisherService publisherService;

    @Transactional
    public LinkedinPostDraft generateDraft(NewsArticle article, ArticleSummary summary) {
        if (draftRepository.existsByArticleIdAndStatusIn(article.getId(), List.of("PENDING_APPROVAL", "APPROVED", "PUBLISHED"))) {
            throw new IllegalStateException("Draft already exists for article " + article.getId());
        }
        String post = llmClient.generate(promptBuilder.linkedInPostPrompt(article, summary.getDetailedSummary()));
        LinkedinPostDraft draft = new LinkedinPostDraft();
        draft.setArticleId(article.getId());
        draft.setTitle(article.getTitle());
        draft.setSourceUrl(article.getUrl());
        draft.setPostBody(post);
        draft.setHashtags(extractHashtags(post));
        draft.setStatus("PENDING_APPROVAL");
        article.setStatus("DRAFTED");
        articleRepository.save(article);
        return draftRepository.save(draft);
    }

    @Transactional
    public LinkedinPostDraft approve(Long id, String approvedBy) {
        LinkedinPostDraft draft = draftRepository.findById(id).orElseThrow();
        if (!"PENDING_APPROVAL".equals(draft.getStatus())) {
            throw new IllegalStateException("Only PENDING_APPROVAL drafts can be approved");
        }
        draft.setStatus("APPROVED");
        draft.setApprovedBy(approvedBy == null || approvedBy.isBlank() ? "chidha2019" : approvedBy);
        draft.setApprovedAt(LocalDateTime.now());
        draft.setUpdatedAt(LocalDateTime.now());
        return draftRepository.save(draft);
    }

    @Transactional
    public LinkedinPostDraft reject(Long id) {
        LinkedinPostDraft draft = draftRepository.findById(id).orElseThrow();
        if ("PUBLISHED".equals(draft.getStatus())) {
            throw new IllegalStateException("Published draft cannot be rejected");
        }
        draft.setStatus("REJECTED");
        draft.setUpdatedAt(LocalDateTime.now());
        return draftRepository.save(draft);
    }

    @Transactional
    public LinkedinPostDraft updateBody(Long id, String body) {
        LinkedinPostDraft draft = draftRepository.findById(id).orElseThrow();
        if ("PUBLISHED".equals(draft.getStatus())) throw new IllegalStateException("Published draft cannot be edited");
        draft.setPostBody(body);
        draft.setHashtags(extractHashtags(body));
        draft.setUpdatedAt(LocalDateTime.now());
        return draftRepository.save(draft);
    }

    @Transactional
    public LinkedinPostDraft publish(Long id) {
        LinkedinPostDraft draft = draftRepository.findById(id).orElseThrow();
        if (!"APPROVED".equals(draft.getStatus())) {
            throw new IllegalStateException("Draft must be APPROVED before publishing");
        }
        String postId = publisherService.publish(draft);
        draft.setStatus("PUBLISHED");
        draft.setLinkedinPostId(postId);
        draft.setPublishedAt(LocalDateTime.now());
        draft.setUpdatedAt(LocalDateTime.now());
        return draftRepository.save(draft);
    }

    private String extractHashtags(String post) {
        if (post == null) return "";
        return String.join(" ", java.util.Arrays.stream(post.split("\\s+"))
                .filter(w -> w.startsWith("#"))
                .toList());
    }
}
