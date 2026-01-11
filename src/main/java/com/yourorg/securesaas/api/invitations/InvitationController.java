package com.yourorg.securesaas.api.invitations;

import com.yourorg.securesaas.app.invitations.InvitationService;
import com.yourorg.securesaas.domain.invitation.Invitation;
import com.yourorg.securesaas.domain.security.Role;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

  private final InvitationService invitationService;

  public InvitationController(InvitationService invitationService) {
    this.invitationService = invitationService;
  }

  @GetMapping
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'INVITATION_READ')")
  public List<InvitationResponse> list(@RequestParam("workspaceId") UUID workspaceId) {
    return invitationService.list(workspaceId).stream().map(this::toResponse).toList();
  }

  @PostMapping
  @PreAuthorize("@workspaceAuth.canAccessWorkspace(#workspaceId, 'INVITATION_WRITE')")
  public InvitationCreatedResponse invite(
      @RequestParam("workspaceId") UUID workspaceId, @Valid @RequestBody InvitationRequest request) {
    InvitationService.CreatedInvitation created =
        invitationService.invite(workspaceId, request.email(), Role.valueOf(request.role()));
    return new InvitationCreatedResponse(created.id(), created.token());
  }

  @PostMapping("/accept")
  public void accept(@Valid @RequestBody InvitationAcceptRequest request) {
    invitationService.accept(request.token(), request.email());
  }

  private InvitationResponse toResponse(Invitation invitation) {
    return new InvitationResponse(
        invitation.getId(),
        invitation.getEmail(),
        invitation.getRole().name(),
        invitation.getStatus().name(),
        invitation.getExpiresAt(),
        invitation.getAcceptedAt());
  }
}
