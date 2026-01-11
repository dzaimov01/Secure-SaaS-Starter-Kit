package com.yourorg.securesaas.api.common;

import java.time.OffsetDateTime;

public record ApiError(String message, String requestId, OffsetDateTime timestamp) {}
