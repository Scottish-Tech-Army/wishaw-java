package org.scottishtecharmy.wishaw_java.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.user.dto.CreateUserRequest;
import org.scottishtecharmy.wishaw_java.user.dto.UpdateUserRequest;
import org.scottishtecharmy.wishaw_java.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CentreRepository centreRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

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

    // ── findAll ─────────────────────────────────────────────────────

    @Test
    void findAll_returnsMappedResponses() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserResponse> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("john");
        assertThat(result.get(0).centreName()).isEqualTo("Wishaw YMCA");
    }

    @Test
    void findAll_emptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.findAll();

        assertThat(result).isEmpty();
    }

    // ── findById ────────────────────────────────────────────────────

    @Test
    void findById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.displayName()).isEqualTo("John Doe");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void findById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── findByCentre ────────────────────────────────────────────────

    @Test
    void findByCentre_returnsFilteredUsers() {
        when(userRepository.findByCentreId(1L)).thenReturn(List.of(testUser));

        List<UserResponse> result = userService.findByCentre(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).centreId()).isEqualTo(1L);
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_success_defaultRole() {
        CreateUserRequest request = new CreateUserRequest("newuser", "pass123", "New User", null, null);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UserResponse response = userService.create(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.role()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_withCentreAndRole() {
        CreateUserRequest request = new CreateUserRequest("admin", "pass123", "Admin", "CENTRE_ADMIN", 1L);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(11L);
            return u;
        });

        UserResponse response = userService.create(request);

        assertThat(response.role()).isEqualTo("CENTRE_ADMIN");
        assertThat(response.centreId()).isEqualTo(1L);
    }

    @Test
    void create_duplicateUsername_throws() {
        CreateUserRequest request = new CreateUserRequest("john", "pass123", "John", null, null);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void create_centreNotFound_throws() {
        CreateUserRequest request = new CreateUserRequest("newuser", "pass123", "New", null, 999L);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(centreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Centre not found");
    }

    // ── update ──────────────────────────────────────────────────────

    @Test
    void update_displayName() {
        UpdateUserRequest request = new UpdateUserRequest("New Name", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.update(1L, request);

        assertThat(response.displayName()).isEqualTo("New Name");
    }

    @Test
    void update_roleAndCentre() {
        Centre newCentre = new Centre();
        newCentre.setId(2L);
        newCentre.setName("Glasgow YMCA");

        UpdateUserRequest request = new UpdateUserRequest(null, "MAIN_ADMIN", 2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(centreRepository.findById(2L)).thenReturn(Optional.of(newCentre));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.update(1L, request);

        assertThat(response.role()).isEqualTo("MAIN_ADMIN");
        assertThat(response.centreId()).isEqualTo(2L);
    }

    @Test
    void update_notFound_throws() {
        UpdateUserRequest request = new UpdateUserRequest("X", null, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── delete ──────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── changePassword ──────────────────────────────────────────────

    @Test
    void changePassword_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldpass", "encoded-password")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("new-encoded");

        userService.changePassword(1L, "oldpass", "newpass");

        verify(userRepository).save(argThat(u -> "new-encoded".equals(u.getPassword())));
    }

    @Test
    void changePassword_wrongCurrent_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, "wrongpass", "newpass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Current password is incorrect");

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(99L, "old", "new"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── toResponse ──────────────────────────────────────────────────

    @Test
    void toResponse_withNullCentre() {
        testUser.setCentre(null);

        UserResponse response = userService.toResponse(testUser);

        assertThat(response.centreId()).isNull();
        assertThat(response.centreName()).isNull();
    }
}

