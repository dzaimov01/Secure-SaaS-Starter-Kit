package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.audit.AuditLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {}
