package com.devsync.security;

import com.devsync.users.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
  private final SecretKey key;
  private final long expirationMinutes;

  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMinutes = expirationMinutes;
  }

  public String generate(User user) {
    Instant now = Instant.now();
    return Jwts.builder()
      .subject(user.getId().toString())
      .claim("email", user.getEmail())
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
      .signWith(key)
      .compact();
  }

  public UUID parseUserId(String token) {
    String subject = Jwts.parser().verifyWith(key).build()
      .parseSignedClaims(token)
      .getPayload()
      .getSubject();
    return UUID.fromString(subject);
  }
}
