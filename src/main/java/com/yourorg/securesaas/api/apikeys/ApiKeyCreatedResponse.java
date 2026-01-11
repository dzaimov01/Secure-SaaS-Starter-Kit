package com.yourorg.securesaas.api.apikeys;

import java.util.UUID;

public record ApiKeyCreatedResponse(UUID id, String prefix, String rawKey) {}
