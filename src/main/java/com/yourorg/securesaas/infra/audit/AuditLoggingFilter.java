package com.yourorg.securesaas.infra.audit;

import com.yourorg.securesaas.domain.audit.AuditLog;
import com.yourorg.securesaas.infra.db.AuditLogRepository;
import com.yourorg.securesaas.infra.security.AuthenticatedPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {

  private final AuditLogRepository auditLogRepository;
  private final AuditExportService auditExportService;

  public AuditLoggingFilter(
      AuditLogRepository auditLogRepository, AuditExportService auditExportService) {
    this.auditLogRepository = auditLogRepository;
    this.auditExportService = auditExportService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestId = resolveRequestId(request);
    request.setAttribute("requestId", requestId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      String action = request.getMethod() + " " + request.getRequestURI();
      String result = response.getStatus() >= 400 ? "FAIL" : "SUCCESS";

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UUID actorId = null;
      String actorEmail = null;
      if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedPrincipal principal) {
        actorId = principal.getUserId();
        actorEmail = principal.getEmail();
      }

      AuditLog log =
          new AuditLog(
              UUID.randomUUID(),
              actorId,
              actorEmail,
              action,
              null,
              result,
              requestId,
              resolveIp(request),
              request.getHeader("User-Agent"),
              OffsetDateTime.now());
      auditLogRepository.save(log);
      auditExportService.export(log);
    }
  }

  private String resolveRequestId(HttpServletRequest request) {
    String requestId = request.getHeader("X-Request-Id");
    if (requestId == null || requestId.isBlank()) {
      return UUID.randomUUID().toString();
    }
    return requestId;
  }

  private String resolveIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
