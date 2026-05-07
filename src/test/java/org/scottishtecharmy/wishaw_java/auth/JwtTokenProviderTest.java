package org.scottishtecharmy.wishaw_java.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scottishtecharmy.wishaw_java.config.JwtConfig;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtConfig config = new JwtConfig();
        // Use reflection to set fields since they're @Value-injected
        try {
            var secretField = JwtConfig.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(config, "WishawYMCAEsportsAcademySecretKeyForJWTTokenGeneration2026!");

            var expirationField = JwtConfig.class.getDeclaredField("expirationMs");
            expirationField.setAccessible(true);
            expirationField.set(config, 86400000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jwtTokenProvider = new JwtTokenProvider(config);
    }

    @Test
    void generateToken_returnsNonEmptyString() {
        String token = jwtTokenProvider.generateToken("john", "USER");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void getUsernameFromToken_returnsCorrectUsername() {
        String token = jwtTokenProvider.generateToken("alice", "USER");

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertThat(username).isEqualTo("alice");
    }

    @Test
    void getRoleFromToken_returnsCorrectRole() {
        String token = jwtTokenProvider.generateToken("alice", "MAIN_ADMIN");

        String role = jwtTokenProvider.getRoleFromToken(token);

        assertThat(role).isEqualTo("MAIN_ADMIN");
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = jwtTokenProvider.generateToken("john", "USER");

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    void validateToken_emptyToken_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }

    @Test
    void validateToken_nullToken_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }

    @Test
    void differentUsers_produceDifferentTokens() {
        String token1 = jwtTokenProvider.generateToken("alice", "USER");
        String token2 = jwtTokenProvider.generateToken("bob", "USER");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void differentRoles_produceDifferentTokens() {
        String token1 = jwtTokenProvider.generateToken("alice", "USER");
        String token2 = jwtTokenProvider.generateToken("alice", "MAIN_ADMIN");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void tokenRoundTrip_preservesAllClaims() {
        String token = jwtTokenProvider.generateToken("bob", "CENTRE_ADMIN");

        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("bob");
        assertThat(jwtTokenProvider.getRoleFromToken(token)).isEqualTo("CENTRE_ADMIN");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }
}

