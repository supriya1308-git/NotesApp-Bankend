package com.notesapp.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
  @Value("${app.jwtSecret}")
  private String jwtSecret;

  @Value("${app.jwtExpirationMs}")
  private long jwtExpirationMs;

  public String generateToken(String username) {
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return Jwts.parserBuilder().setSigningKey(key).build()
               .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
