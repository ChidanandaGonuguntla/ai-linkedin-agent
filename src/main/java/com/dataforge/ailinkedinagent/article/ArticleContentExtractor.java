package com.dataforge.ailinkedinagent.article;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticleContentExtractor {
    @Value("${agent.crawler.connect-timeout-ms:10000}")
    private int timeoutMs;

    @Value("${agent.crawler.max-content-chars:12000}")
    private int maxChars;

    public String extractReadableText(String url) {
        try {
            var document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 ai-linkedin-agent/1.0")
                    .timeout(timeoutMs)
                    .followRedirects(true)
                    .get();
            document.select("script, style, nav, footer, header, aside, form, noscript").remove();
            String title = document.title() == null ? "" : document.title();
            String body = document.body() == null ? "" : document.body().text();
            String text = (title + "\n\n" + body).trim();
            return text.length() > maxChars ? text.substring(0, maxChars) : text;
        } catch (Exception e) {
            log.warn("Content extraction failed for url={}", url, e);
            return "";
        }
    }
}
