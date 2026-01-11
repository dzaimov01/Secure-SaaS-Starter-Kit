package com.yourorg.securesaas.infra.audit;

import com.yourorg.securesaas.domain.audit.AuditLog;

public interface AuditExportService {
  void export(AuditLog auditLog);
}
