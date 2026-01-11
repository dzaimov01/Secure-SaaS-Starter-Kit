package com.yourorg.securesaas.api.apikeys;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApiKeyRequest(@NotBlank @Size(min = 2, max = 200) String name) {}
