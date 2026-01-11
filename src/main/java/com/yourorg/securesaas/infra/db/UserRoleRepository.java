package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.security.UserRole;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
  List<UserRole> findByUserId(UUID userId);
}
