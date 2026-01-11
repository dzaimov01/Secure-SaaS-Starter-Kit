package com.yourorg.securesaas.infra.security;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

  private final SecureRandom secureRandom = new SecureRandom();

  public String generateToken(int bytes) {
    byte[] buffer = new byte[bytes];
    secureRandom.nextBytes(buffer);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
  }
}
