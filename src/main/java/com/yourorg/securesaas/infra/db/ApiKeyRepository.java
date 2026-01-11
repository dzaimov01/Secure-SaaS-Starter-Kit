package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.apikey.ApiKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
  List<ApiKey> findByWorkspaceId(UUID workspaceId);

  List<ApiKey> findByPrefix(String prefix);

  Optional<ApiKey> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
