package com.yourorg.securesaas.app.apikeys;

import com.yourorg.securesaas.domain.apikey.ApiKey;
import com.yourorg.securesaas.infra.db.ApiKeyRepository;
import com.yourorg.securesaas.infra.security.TokenGenerator;
import com.yourorg.securesaas.infra.security.TokenHasher;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiKeyService {

  private final ApiKeyRepository apiKeyRepository;
  private final TokenGenerator tokenGenerator;
  private final TokenHasher tokenHasher;

  public ApiKeyService(
      ApiKeyRepository apiKeyRepository, TokenGenerator tokenGenerator, TokenHasher tokenHasher) {
    this.apiKeyRepository = apiKeyRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenHasher = tokenHasher;
  }

  @Transactional
  public CreatedApiKey createKey(UUID workspaceId, String name) {
    String prefix = tokenGenerator.generateToken(6);
    String secret = tokenGenerator.generateToken(24);
    String rawKey = prefix + "." + secret;
    ApiKey apiKey =
        new ApiKey(UUID.randomUUID(), workspaceId, name, prefix, tokenHasher.sha256(secret));
    apiKeyRepository.save(apiKey);
    return new CreatedApiKey(apiKey.getId(), prefix, rawKey);
  }

  public List<ApiKey> listKeys(UUID workspaceId) {
    return apiKeyRepository.findByWorkspaceId(workspaceId);
  }

  @Transactional
  public void revoke(UUID workspaceId, UUID apiKeyId) {
    ApiKey apiKey = apiKeyRepository.findByIdAndWorkspaceId(apiKeyId, workspaceId).orElseThrow();
    apiKey.revoke();
    apiKeyRepository.save(apiKey);
  }

  public record CreatedApiKey(UUID id, String prefix, String rawKey) {}
}
