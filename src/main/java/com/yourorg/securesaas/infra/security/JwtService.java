package com.yourorg.securesaas.infra.security;

import com.yourorg.securesaas.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  private final AppProperties properties;
  private final SecretKey secretKey;

  public JwtService(AppProperties properties) {
    this.properties = properties;
    this.secretKey =
        Keys.hmacShaKeyFor(
            properties.getSecurity().getJwt().getSigningKey().getBytes(StandardCharsets.UTF_8));
  }

  public String issueAccessToken(UUID userId, String email, List<String> roles) {
    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime expiry = now.plus(properties.getSecurity().getJwt().getAccessTokenTtl());
    return Jwts.builder()
        .issuer(properties.getSecurity().getJwt().getIssuer())
        .subject(userId.toString())
        .claim("email", email)
        .claim("roles", roles)
        .issuedAt(Date.from(now.toInstant()))
        .expiration(Date.from(expiry.toInstant()))
        .signWith(secretKey)
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
