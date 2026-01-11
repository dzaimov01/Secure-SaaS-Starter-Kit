package com.yourorg.securesaas.api.workspaces;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkspaceRequest(@NotBlank @Size(min = 2, max = 200) String name) {}
