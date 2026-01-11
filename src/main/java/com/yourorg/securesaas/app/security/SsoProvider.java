package com.yourorg.securesaas.app.security;

import java.util.Optional;

public interface SsoProvider {
  Optional<SsoUser> exchangeAuthorizationCode(String code);
}
