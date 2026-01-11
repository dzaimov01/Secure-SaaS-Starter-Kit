package com.yourorg.securesaas.api.invitations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InvitationRequest(@Email @NotBlank String email, @NotBlank String role) {}
