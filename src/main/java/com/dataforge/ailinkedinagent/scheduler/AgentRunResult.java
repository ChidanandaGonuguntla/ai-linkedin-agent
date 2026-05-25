package com.dataforge.ailinkedinagent.scheduler;

public record AgentRunResult(
        int sourcesProcessed,
        int articlesSaved,
        int articlesScored,
        int draftsGenerated,
        String status
) {}
