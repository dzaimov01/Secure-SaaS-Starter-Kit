package com.yourorg.securesaas.api.projects;

import java.util.UUID;

public record ProjectResponse(UUID id, String name, String description) {}
