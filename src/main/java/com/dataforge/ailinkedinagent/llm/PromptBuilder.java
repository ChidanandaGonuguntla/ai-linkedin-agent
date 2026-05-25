package com.dataforge.ailinkedinagent.llm;

import com.dataforge.ailinkedinagent.article.NewsArticle;
import org.springframework.stereotype.Service;

@Service
public class PromptBuilder {
    public String summaryPrompt(NewsArticle article) {
        return """
                Summarize this AI article for an enterprise software engineer.
                Rules:
                - Do not invent facts.
                - Mention uncertainty when article details are incomplete.
                - Output exactly three sections: Short Summary, Why It Matters, Engineering Angle.

                Title: %s
                URL: %s
                Content:
                %s
                """.formatted(article.getTitle(), article.getUrl(), article.getRawContent());
    }

    public String linkedInPostPrompt(NewsArticle article, String summary) {
        return """
                Write a LinkedIn post for Chidananda Naidu, a senior Java/Spring Boot engineer building enterprise AI platforms.

                Requirements:
                - Approval-first professional draft.
                - Under 220 words.
                - Strong first line.
                - Connect the AI update to enterprise architecture, semantic layers, RAG, workflow automation, governance, or data engineering when relevant.
                - Do not pretend personal involvement in the article.
                - Do not invent facts.
                - Include the source URL at the end.
                - Include 5 to 8 hashtags.
                - Return only the final LinkedIn post.

                Article title: %s
                Article URL: %s
                Summary:
                %s
                """.formatted(article.getTitle(), article.getUrl(), summary);
    }
}
