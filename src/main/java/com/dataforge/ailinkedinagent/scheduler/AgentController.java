package com.dataforge.ailinkedinagent.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {
    private final AiLinkedinAgentOrchestrator orchestrator;

    @PostMapping("/run")
    public AgentRunResult runNow() {
        return orchestrator.run();
    }
}
