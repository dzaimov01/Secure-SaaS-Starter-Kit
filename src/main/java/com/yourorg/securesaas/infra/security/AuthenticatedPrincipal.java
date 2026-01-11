package com.yourorg.securesaas.infra.security;

import java.util.UUID;

public class AuthenticatedPrincipal {

  private final UUID userId;
  private final String email;
  private final UUID workspaceId;
  private final boolean apiKey;

  public AuthenticatedPrincipal(UUID userId, String email, UUID workspaceId, boolean apiKey) {
    this.userId = userId;
    this.email = email;
    this.workspaceId = workspaceId;
    this.apiKey = apiKey;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public UUID getWorkspaceId() {
    return workspaceId;
  }

  public boolean isApiKey() {
    return apiKey;
  }
}
