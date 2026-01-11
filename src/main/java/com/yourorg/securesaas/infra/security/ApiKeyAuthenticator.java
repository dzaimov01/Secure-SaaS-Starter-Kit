package com.yourorg.securesaas.infra.security;

import com.yourorg.securesaas.domain.apikey.ApiKey;
import com.yourorg.securesaas.infra.db.ApiKeyRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticator {

  private final ApiKeyRepository apiKeyRepository;
  private final TokenHasher tokenHasher;

  public ApiKeyAuthenticator(ApiKeyRepository apiKeyRepository, TokenHasher tokenHasher) {
    this.apiKeyRepository = apiKeyRepository;
    this.tokenHasher = tokenHasher;
  }

  public Optional<ApiKey> authenticate(String apiKeyHeader) {
    String[] parts = apiKeyHeader.split("\\.");
    if (parts.length != 2) {
      return Optional.empty();
    }
    String prefix = parts[0];
    String secret = parts[1];
    String hash = tokenHasher.sha256(secret);
    return apiKeyRepository.findByPrefix(prefix).stream()
        .filter(apiKey -> !apiKey.isRevoked())
        .filter(apiKey -> apiKey.getKeyHash().equals(hash))
        .findFirst();
  }
}
