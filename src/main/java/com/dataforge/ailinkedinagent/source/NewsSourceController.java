package com.dataforge.ailinkedinagent.source;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
@RequiredArgsConstructor
public class NewsSourceController {
    private final NewsSourceRepository repository;

    @GetMapping
    public List<NewsSource> all() {
        return repository.findAll();
    }

    @PostMapping
    public NewsSource save(@RequestBody NewsSource source) {
        return repository.save(source);
    }

    @PatchMapping("/{id}/enabled/{enabled}")
    public NewsSource setEnabled(@PathVariable Long id, @PathVariable boolean enabled) {
        NewsSource source = repository.findById(id).orElseThrow();
        source.setEnabled(enabled);
        return repository.save(source);
    }
}
