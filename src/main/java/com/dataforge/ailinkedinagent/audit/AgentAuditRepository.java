package com.dataforge.ailinkedinagent.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentAuditRepository extends JpaRepository<AgentAuditLog, Long> {}
