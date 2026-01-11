package com.yourorg.securesaas.infra.db;

import com.yourorg.securesaas.domain.invitation.Invitation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
  List<Invitation> findByWorkspaceId(UUID workspaceId);

  Optional<Invitation> findByTokenHash(String tokenHash);
}
