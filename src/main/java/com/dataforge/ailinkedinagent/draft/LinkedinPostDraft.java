package com.dataforge.ailinkedinagent.draft;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "linkedin_post_draft")
public class LinkedinPostDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long articleId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String sourceUrl;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String postBody;
    @Column(columnDefinition = "TEXT")
    private String hashtags;
    private String status = "PENDING_APPROVAL";
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime publishedAt;
    private String linkedinPostId;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
