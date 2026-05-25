package com.dataforge.ailinkedinagent.draft;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
public class DraftApprovalController {
    private final LinkedinPostDraftRepository repository;
    private final LinkedinDraftService service;

    @GetMapping
    public List<LinkedinPostDraft> all(@RequestParam(required = false) String status) {
        return status == null ? repository.findAll() : repository.findByStatusOrderByCreatedAtDesc(status);
    }

    @GetMapping("/pending")
    public List<LinkedinPostDraft> pending() {
        return repository.findByStatusOrderByCreatedAtDesc("PENDING_APPROVAL");
    }

    @PostMapping("/{id}/approve")
    public LinkedinPostDraft approve(@PathVariable Long id, @RequestParam(defaultValue = "chidha2019") String approvedBy) {
        return service.approve(id, approvedBy);
    }

    @PostMapping("/{id}/reject")
    public LinkedinPostDraft reject(@PathVariable Long id) {
        return service.reject(id);
    }

    @PutMapping("/{id}/body")
    public LinkedinPostDraft updateBody(@PathVariable Long id, @RequestBody UpdateDraftBody request) {
        return service.updateBody(id, request.body());
    }

    @PostMapping("/{id}/publish")
    public LinkedinPostDraft publish(@PathVariable Long id) {
        return service.publish(id);
    }

    public record UpdateDraftBody(@NotBlank String body) {}
}
