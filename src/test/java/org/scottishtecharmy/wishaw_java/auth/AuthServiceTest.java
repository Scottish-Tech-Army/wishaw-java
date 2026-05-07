package org.scottishtecharmy.wishaw_java.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.auth.dto.AuthResponse;
import org.scottishtecharmy.wishaw_java.auth.dto.LoginRequest;
import org.scottishtecharmy.wishaw_java.auth.dto.RegisterRequest;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.user.Role;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CentreRepository centreRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks private AuthService authService;

    private User testUser;
    private Centre testCentre;

    @BeforeEach
    void setUp() {
        testCentre = new Centre();
        testCentre.setId(1L);
        testCentre.setName("Wishaw YMCA");
        testCentre.setCode("WISHAW");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john");
        testUser.setPassword("encoded-password");
        testUser.setDisplayName("John Doe");
        testUser.setRole(Role.USER);
        testUser.setCentre(testCentre);
    }

    // ── Register ──────────────────────────────────────────────────��──

    @Test
    void register_success_withDefaultRole() {
        RegisterRequest request = new RegisterRequest("john", "pass123", "John Doe", null, null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateToken("john", "USER")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.centreId()).isNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_success_withCentreAndRole() {
        RegisterRequest request = new RegisterRequest("admin1", "pass123", "Admin", "CENTRE_ADMIN", 1L);
        when(userRepository.existsByUsername("admin1")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded-password");
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtTokenProvider.generateToken("admin1", "CENTRE_ADMIN")).thenReturn("jwt-token-admin");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token-admin");
        assertThat(response.role()).isEqualTo("CENTRE_ADMIN");
        assertThat(response.centreId()).isEqualTo(1L);
    }

    @Test
    void register_duplicateUsername_throws() {
        RegisterRequest request = new RegisterRequest("john", "pass123", "John", null, null);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_centreNotFound_throws() {
        RegisterRequest request = new RegisterRequest("john", "pass123", "John", null, 999L);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(centreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Centre not found");
    }

    @Test
    void register_displaysNameDefaultsToUsername_whenNull() {
        RegisterRequest request = new RegisterRequest("john", "pass123", null, null, null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateToken(anyString(), anyString())).thenReturn("token");

        authService.register(request);

        verify(userRepository).save(argThat(u -> "john".equals(u.getDisplayName())));
    }

    // ── Login ───���────────────────────────────────────────────────────

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("john", "pass123");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("pass123", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("john", "USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.centreId()).isEqualTo(1L);
    }

    @Test
    void login_userNotFound_throws() {
        LoginRequest request = new LoginRequest("unknown", "pass123");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void login_wrongPassword_throws() {
        LoginRequest request = new LoginRequest("john", "wrongpass");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }
}

