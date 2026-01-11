package com.yourorg.securesaas.api.invitations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InvitationAcceptRequest(@NotBlank String token, @Email @NotBlank String email) {}
