package org.scottishtecharmy.wishaw_java.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.scottishtecharmy.wishaw_java.model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles JWT creation and validation.
 *
 * The JWT payload contains:
 *   sub           – username (e.g. "@alex_gamer")
 *   studentId     – backend numeric ID
 *   role          – "ROLE_STUDENT" or "ROLE_ADMIN"
 *   playerUsername – same as sub (for legacy frontend compat)
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }

    /** Generate a signed JWT for the given student. */
    public String generateToken(Student student) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentId", student.getId());
        claims.put("role", student.getRole());
        claims.put("playerUsername", student.getUsername());

        return Jwts.builder()
                .claims(claims)
                .subject(student.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Extract all claims from the token. Throws on invalid/expired. */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractStudentId(String token) {
        return extractClaims(token).get("studentId", Long.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
