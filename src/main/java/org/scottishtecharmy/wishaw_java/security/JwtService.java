package org.scottishtecharmy.wishaw_java.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.scottishtecharmy.wishaw_java.config.AppProperties;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final AppProperties appProperties;

    public JwtService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String generateAccessToken(UserAccount userAccount) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userAccount.getId())
                .claim("email", userAccount.getEmail())
                .claim("role", userAccount.getRole().name())
                .claim("tokenType", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(appProperties.getJwt().getAccessTokenMinutes(), ChronoUnit.MINUTES)))
                .signWith(secretKey())
                .compact();
    }

    public String generateRefreshToken(UserAccount userAccount) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userAccount.getId())
                .claim("tokenType", "REFRESH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(appProperties.getJwt().getRefreshTokenDays(), ChronoUnit.DAYS)))
                .signWith(secretKey())
                .compact();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(parseClaims(token).get("tokenType", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(parseClaims(token).get("tokenType", String.class));
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        byte[] keyBytes = appProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
