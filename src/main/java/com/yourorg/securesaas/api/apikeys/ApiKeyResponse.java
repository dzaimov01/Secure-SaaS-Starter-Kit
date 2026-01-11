package com.yourorg.securesaas.api.apikeys;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApiKeyResponse(UUID id, String name, String prefix, OffsetDateTime createdAt, boolean revoked) {}
