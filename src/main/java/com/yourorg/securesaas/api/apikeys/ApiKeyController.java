package com.yourorg.securesaas.api.apikeys;

import com.yourorg.securesaas.app.apikeys.ApiKeyService;
import com.yourorg.securesaas.domain.apikey.ApiKey;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {

  private final ApiKeyService apiKeyService;

  public ApiKeyController(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @GetMapping
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'API_KEY_READ')")
  public List<ApiKeyResponse> list(@RequestParam("workspaceId") UUID workspaceId) {
    return apiKeyService.listKeys(workspaceId).stream().map(this::toResponse).toList();
  }

  @PostMapping
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'API_KEY_WRITE')")
  public ApiKeyCreatedResponse create(
      @RequestParam("workspaceId") UUID workspaceId, @Valid @RequestBody ApiKeyRequest request) {
    ApiKeyService.CreatedApiKey created = apiKeyService.createKey(workspaceId, request.name());
    return new ApiKeyCreatedResponse(created.id(), created.prefix(), created.rawKey());
  }

  @PostMapping("/{apiKeyId}/revoke")
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'API_KEY_WRITE')")
  public void revoke(
      @RequestParam("workspaceId") UUID workspaceId, @PathVariable UUID apiKeyId) {
    apiKeyService.revoke(workspaceId, apiKeyId);
  }

  private ApiKeyResponse toResponse(ApiKey apiKey) {
    return new ApiKeyResponse(
        apiKey.getId(), apiKey.getName(), apiKey.getPrefix(), apiKey.getCreatedAt(), apiKey.isRevoked());
  }
}
