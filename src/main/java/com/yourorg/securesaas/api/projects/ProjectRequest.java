package com.yourorg.securesaas.api.projects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
    @NotBlank @Size(min = 2, max = 200) String name,
    @Size(max = 1000) String description) {}
