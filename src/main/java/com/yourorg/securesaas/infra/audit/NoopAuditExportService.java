package com.yourorg.securesaas.infra.audit;

import com.yourorg.securesaas.domain.audit.AuditLog;
import org.springframework.stereotype.Service;

@Service
public class NoopAuditExportService implements AuditExportService {

  @Override
  public void export(AuditLog auditLog) {
    // Intentionally no-op. Replace with SIEM integration.
  }
}
