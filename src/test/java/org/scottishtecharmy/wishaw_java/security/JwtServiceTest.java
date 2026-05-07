package org.scottishtecharmy.wishaw_java.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scottishtecharmy.wishaw_java.config.AppProperties;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class JwtServiceTest {

    @Test
    void generatesAndParsesAccessAndRefreshTokens() {
        AppProperties properties = new AppProperties();
        properties.getJwt().setSecret("change-me-change-me-change-me-change-me-change-me-change-me");
        properties.getJwt().setAccessTokenMinutes(60);
        properties.getJwt().setRefreshTokenDays(7);
        JwtService jwtService = new JwtService(properties);

        UserAccount userAccount = UserAccount.builder()
                .id("u99")
                .email("test@wymca.org")
                .role(UserRole.ADMIN)
                .centre(new Centre("c1", "Wishaw YMCA", "Wishaw"))
                .build();

        String accessToken = jwtService.generateAccessToken(userAccount);
        String refreshToken = jwtService.generateRefreshToken(userAccount);

        assertTrue(jwtService.isValid(accessToken));
        assertTrue(jwtService.isAccessToken(accessToken));
        assertTrue(jwtService.isRefreshToken(refreshToken));
        assertEquals("u99", jwtService.extractSubject(accessToken));
        assertEquals("ADMIN", jwtService.extractRole(accessToken));
    }
}
