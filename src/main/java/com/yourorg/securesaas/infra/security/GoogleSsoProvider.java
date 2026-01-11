package com.yourorg.securesaas.infra.security;

import com.yourorg.securesaas.app.security.SsoProvider;
import com.yourorg.securesaas.app.security.SsoUser;
import com.yourorg.securesaas.config.AppProperties;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GoogleSsoProvider implements SsoProvider {

  private final AppProperties properties;

  public GoogleSsoProvider(AppProperties properties) {
    this.properties = properties;
  }

  @Override
  public Optional<SsoUser> exchangeAuthorizationCode(String code) {
    if (!properties.getSecurity().getOauth().isGoogleEnabled()) {
      return Optional.empty();
    }
    return Optional.empty();
  }
}
