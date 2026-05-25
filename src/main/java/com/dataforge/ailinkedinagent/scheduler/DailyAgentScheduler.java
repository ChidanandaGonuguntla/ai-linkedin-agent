package com.dataforge.ailinkedinagent.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyAgentScheduler {
    private final AiLinkedinAgentOrchestrator orchestrator;

    @Value("${agent.scheduler.enabled:true}")
    private boolean enabled;

    @Scheduled(cron = "${agent.scheduler.cron}")
    public void runDailyAgent() {
        if (!enabled) {
            log.info("Daily agent scheduler disabled");
            return;
        }
        log.info("Starting scheduled AI LinkedIn agent");
        AgentRunResult result = orchestrator.run();
        log.info("Scheduled AI LinkedIn agent completed: {}", result);
    }
}
