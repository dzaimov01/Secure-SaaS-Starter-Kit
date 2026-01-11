package com.yourorg.securesaas.app.invitations;

import com.yourorg.securesaas.domain.invitation.Invitation;
import com.yourorg.securesaas.domain.security.Role;
import com.yourorg.securesaas.domain.workspace.WorkspaceMembership;
import com.yourorg.securesaas.infra.db.InvitationRepository;
import com.yourorg.securesaas.infra.db.UserRepository;
import com.yourorg.securesaas.infra.db.WorkspaceMembershipRepository;
import com.yourorg.securesaas.infra.security.TokenGenerator;
import com.yourorg.securesaas.infra.security.TokenHasher;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationService {

  private final InvitationRepository invitationRepository;
  private final WorkspaceMembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final TokenGenerator tokenGenerator;
  private final TokenHasher tokenHasher;

  public InvitationService(
      InvitationRepository invitationRepository,
      WorkspaceMembershipRepository membershipRepository,
      UserRepository userRepository,
      TokenGenerator tokenGenerator,
      TokenHasher tokenHasher) {
    this.invitationRepository = invitationRepository;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenHasher = tokenHasher;
  }

  @Transactional
  public CreatedInvitation invite(UUID workspaceId, String email, Role role) {
    String tokenRaw = tokenGenerator.generateToken(48);
    Invitation invitation =
        new Invitation(
            UUID.randomUUID(),
            workspaceId,
            email,
            tokenHasher.sha256(tokenRaw),
            role,
            OffsetDateTime.now().plusDays(7));
    invitationRepository.save(invitation);
    return new CreatedInvitation(invitation.getId(), tokenRaw);
  }

  public List<Invitation> list(UUID workspaceId) {
    return invitationRepository.findByWorkspaceId(workspaceId);
  }

  @Transactional
  public void accept(String tokenRaw, String email) {
    Invitation invitation =
        invitationRepository
            .findByTokenHash(tokenHasher.sha256(tokenRaw))
            .orElseThrow(() -> new IllegalArgumentException("invalid invitation"));
    if (invitation.isExpired()) {
      throw new IllegalArgumentException("invitation expired");
    }

    var user =
        userRepository
            .findByEmail(email.toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

    boolean alreadyMember =
        membershipRepository
            .findByWorkspaceIdAndUserId(invitation.getWorkspaceId(), user.getId())
            .isPresent();
    if (!alreadyMember) {
      membershipRepository.save(
          new WorkspaceMembership(
              UUID.randomUUID(), invitation.getWorkspaceId(), user.getId(), invitation.getRole()));
    }
    invitation.accept();
    invitationRepository.save(invitation);
  }

  public record CreatedInvitation(UUID id, String token) {}
}
