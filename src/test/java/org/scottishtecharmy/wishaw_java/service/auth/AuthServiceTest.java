package org.scottishtecharmy.wishaw_java.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.dto.request.LoginRequest;
import org.scottishtecharmy.wishaw_java.dto.response.AuthResponse;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession httpSession;

    @Mock
    private HttpSession existingSession;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginAuthenticatesStoresSessionContextAndReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("superadmin");
        request.setPassword("admin123");
        UserAccount user = user(1L, "superadmin", "Super Admin", Role.SUPER_ADMIN);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("superadmin");
        when(httpServletRequest.getSession(false)).thenReturn(existingSession);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);
        when(authenticatedUserService.getByUsername("superadmin")).thenReturn(user);

        AuthResponse response = authService.login(request, httpServletRequest);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("superadmin");
        assertThat(response.getRole()).isEqualTo("SUPER_ADMIN");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
        verify(existingSession).invalidate();
        verify(httpSession).setAttribute(any(), any());
        verify(httpSession).setAttribute(
                org.mockito.ArgumentMatchers.eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                any()
        );
    }

    @Test
    void logoutInvalidatesExistingSessionAndClearsSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(httpServletRequest.getSession(false)).thenReturn(httpSession);

        authService.logout(httpServletRequest);

        verify(httpSession).invalidate();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void meReturnsAuthenticatedUserSummary() {
        UserAccount user = user(2L, "player1", "Test Player", Role.PLAYER);
        when(authenticatedUserService.getCurrentUser(authentication)).thenReturn(user);

        AuthResponse response = authService.me(authentication);

        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getUsername()).isEqualTo("player1");
        assertThat(response.getDisplayName()).isEqualTo("Test Player");
        assertThat(response.getRole()).isEqualTo("PLAYER");
        assertThat(response.getMessage()).isEqualTo("Authenticated user");
    }

    private UserAccount user(Long id, String username, String displayName, Role role) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setRole(role);
        return user;
    }
}
