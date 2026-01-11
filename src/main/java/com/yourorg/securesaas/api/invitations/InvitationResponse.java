package com.yourorg.securesaas.api.invitations;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InvitationResponse(
    UUID id,
    String email,
    String role,
    String status,
    OffsetDateTime expiresAt,
    OffsetDateTime acceptedAt) {}
