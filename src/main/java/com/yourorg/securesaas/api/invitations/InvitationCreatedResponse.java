package com.yourorg.securesaas.api.invitations;

import java.util.UUID;

public record InvitationCreatedResponse(UUID id, String token) {}
