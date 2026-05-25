package com.dataforge.ailinkedinagent.article;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCrawlerService {
    private final NewsArticleRepository articleRepository;
    private final ArticleContentExtractor contentExtractor;
    private final ArticleDeduplicationService deduplicationService;

    @Transactional
    public int crawlRssSource(Long sourceId, String rssUrl, int maxEntries) {
        int saved = 0;
        try {
            var feed = new SyndFeedInput().build(new XmlReader(new URL(rssUrl)));
            for (SyndEntry entry : feed.getEntries()) {
                if (saved >= maxEntries) break;
                String url = entry.getLink();
                if (url == null || url.isBlank() || articleRepository.existsByUrl(url)) continue;
                String content = contentExtractor.extractReadableText(url);
                String hash = deduplicationService.hash(entry.getTitle() + "\n" + content);
                if (articleRepository.existsByContentHash(hash)) continue;

                NewsArticle article = new NewsArticle();
                article.setSourceId(sourceId);
                article.setTitle(entry.getTitle() == null ? "Untitled" : entry.getTitle());
                article.setUrl(url);
                article.setAuthor(entry.getAuthor());
                article.setRawContent(content);
                article.setContentHash(hash);
                article.setStatus("NEW");
                if (entry.getPublishedDate() != null) {
                    article.setPublishedAt(entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                } else {
                    article.setPublishedAt(LocalDateTime.now());
                }
                articleRepository.save(article);
                saved++;
            }
        } catch (Exception e) {
            log.error("Failed to crawl RSS source sourceId={}, url={}", sourceId, rssUrl, e);
        }
        return saved;
    }
}
