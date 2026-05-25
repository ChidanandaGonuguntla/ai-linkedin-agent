package com.dataforge.ailinkedinagent.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentAuditService {
    private final AgentAuditRepository repository;

    public void log(String action, String entityType, Long entityId, String status, String message) {
        AgentAuditLog log = new AgentAuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setStatus(status);
        log.setMessage(message);
        repository.save(log);
    }
}
