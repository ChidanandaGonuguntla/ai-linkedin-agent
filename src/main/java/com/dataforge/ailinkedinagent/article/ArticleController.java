package com.dataforge.ailinkedinagent.article;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final NewsArticleRepository repository;

    @GetMapping
    public List<NewsArticle> recent(@RequestParam(defaultValue = "25") int limit) {
        return repository.findAll(PageRequest.of(0, Math.min(limit, 100))).getContent();
    }

    @GetMapping("/{id}")
    public NewsArticle byId(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
