package com.yourorg.securesaas.api.auth;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(@NotBlank String authorizationCode) {}
