package com.yourorg.securesaas.domain.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

  @Id
  private UUID id;

  @Column(name = "actor_id")
  private UUID actorId;

  @Column(name = "actor_email", length = 255)
  private String actorEmail;

  @Column(name = "action", nullable = false, length = 120)
  private String action;

  @Column(name = "target", length = 255)
  private String target;

  @Column(name = "result", nullable = false, length = 40)
  private String result;

  @Column(name = "request_id", nullable = false, length = 120)
  private String requestId;

  @Column(name = "ip_address", length = 64)
  private String ipAddress;

  @Column(name = "user_agent", length = 255)
  private String userAgent;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  protected AuditLog() {}

  public AuditLog(
      UUID id,
      UUID actorId,
      String actorEmail,
      String action,
      String target,
      String result,
      String requestId,
      String ipAddress,
      String userAgent,
      OffsetDateTime createdAt) {
    this.id = id;
    this.actorId = actorId;
    this.actorEmail = actorEmail;
    this.action = action;
    this.target = target;
    this.result = result;
    this.requestId = requestId;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }
}
