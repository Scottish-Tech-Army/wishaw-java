package org.scottishtecharmy.wishaw_java.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.group.dto.GameGroupRequest;
import org.scottishtecharmy.wishaw_java.group.dto.GameGroupResponse;
import org.scottishtecharmy.wishaw_java.user.Role;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameGroupServiceTest {

    @Mock private GameGroupRepository gameGroupRepository;
    @Mock private CentreRepository centreRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private GameGroupService gameGroupService;

    private GameGroup testGroup;
    private Centre testCentre;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCentre = new Centre();
        testCentre.setId(1L);
        testCentre.setName("Wishaw YMCA");
        testCentre.setCode("WISHAW");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john");
        testUser.setDisplayName("John Doe");
        testUser.setRole(Role.USER);

        testGroup = new GameGroup();
        testGroup.setId(1L);
        testGroup.setName("Minecraft Squad");
        testGroup.setCentre(testCentre);
        testGroup.setMembers(new HashSet<>());
    }

    // ── findAll ─────────────────────────────────────────────────────

    @Test
    void findAll_returnsMappedResponses() {
        when(gameGroupRepository.findAll()).thenReturn(List.of(testGroup));

        List<GameGroupResponse> result = gameGroupService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Minecraft Squad");
        assertThat(result.get(0).centreName()).isEqualTo("Wishaw YMCA");
        assertThat(result.get(0).members()).isEmpty();
    }

    @Test
    void findAll_emptyList() {
        when(gameGroupRepository.findAll()).thenReturn(List.of());
        assertThat(gameGroupService.findAll()).isEmpty();
    }

    // ── findById ────────────────────────────────────────────────────

    @Test
    void findById_success() {
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        GameGroupResponse response = gameGroupService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Minecraft Squad");
    }

    @Test
    void findById_notFound_throws() {
        when(gameGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GameGroup not found");
    }

    // ── findByCentre ────────────────────────────────────────────────

    @Test
    void findByCentre_returnsFilteredGroups() {
        when(gameGroupRepository.findByCentreId(1L)).thenReturn(List.of(testGroup));

        List<GameGroupResponse> result = gameGroupService.findByCentre(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).centreId()).isEqualTo(1L);
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_success() {
        GameGroupRequest request = new GameGroupRequest("Fortnite Team", 1L);
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(gameGroupRepository.save(any(GameGroup.class))).thenAnswer(inv -> {
            GameGroup g = inv.getArgument(0);
            g.setId(2L);
            g.setMembers(new HashSet<>());
            return g;
        });

        GameGroupResponse response = gameGroupService.create(request);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("Fortnite Team");
    }

    @Test
    void create_centreNotFound_throws() {
        GameGroupRequest request = new GameGroupRequest("Team", 999L);
        when(centreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }

    // ── update ──────────────────────────────────────────────────────

    @Test
    void update_success() {
        GameGroupRequest request = new GameGroupRequest("Updated Group", 1L);
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(gameGroupRepository.save(any(GameGroup.class))).thenAnswer(inv -> inv.getArgument(0));

        GameGroupResponse response = gameGroupService.update(1L, request);

        assertThat(response.name()).isEqualTo("Updated Group");
    }

    @Test
    void update_notFound_throws() {
        GameGroupRequest request = new GameGroupRequest("X", 1L);
        when(gameGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.update(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GameGroup not found");
    }

    @Test
    void update_centreNotFound_throws() {
        GameGroupRequest request = new GameGroupRequest("X", 999L);
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(centreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.update(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }

    // ── addMember ───────────────────────────────────────────────────

    @Test
    void addMember_success() {
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gameGroupRepository.save(any(GameGroup.class))).thenAnswer(inv -> inv.getArgument(0));

        GameGroupResponse response = gameGroupService.addMember(1L, 1L);

        assertThat(response.members()).hasSize(1);
        assertThat(response.members().get(0).username()).isEqualTo("john");
    }

    @Test
    void addMember_groupNotFound_throws() {
        when(gameGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.addMember(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GameGroup not found");
    }

    @Test
    void addMember_userNotFound_throws() {
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.addMember(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── removeMember ────────────────────────────────────────────────

    @Test
    void removeMember_success() {
        testGroup.getMembers().add(testUser);
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gameGroupRepository.save(any(GameGroup.class))).thenAnswer(inv -> inv.getArgument(0));

        GameGroupResponse response = gameGroupService.removeMember(1L, 1L);

        assertThat(response.members()).isEmpty();
    }

    @Test
    void removeMember_groupNotFound_throws() {
        when(gameGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameGroupService.removeMember(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GameGroup not found");
    }

    // ── delete ──────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(gameGroupRepository.existsById(1L)).thenReturn(true);

        gameGroupService.delete(1L);

        verify(gameGroupRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(gameGroupRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> gameGroupService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GameGroup not found");
    }

    // ── toResponse with null centre ───────────────��─────────────────

    @Test
    void toResponse_withNullCentre() {
        testGroup.setCentre(null);
        when(gameGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        GameGroupResponse response = gameGroupService.findById(1L);

        assertThat(response.centreId()).isNull();
        assertThat(response.centreName()).isNull();
    }
}

