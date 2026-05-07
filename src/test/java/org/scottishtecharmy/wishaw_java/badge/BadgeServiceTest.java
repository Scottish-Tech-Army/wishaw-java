package org.scottishtecharmy.wishaw_java.badge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.badge.dto.BadgeRequest;
import org.scottishtecharmy.wishaw_java.badge.dto.BadgeResponse;
import org.scottishtecharmy.wishaw_java.badge.dto.SubBadgeRequest;
import org.scottishtecharmy.wishaw_java.badge.dto.SubBadgeResponse;
import org.scottishtecharmy.wishaw_java.module.Module;
import org.scottishtecharmy.wishaw_java.module.ModuleRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock private BadgeRepository badgeRepository;
    @Mock private SubBadgeRepository subBadgeRepository;
    @Mock private ModuleRepository moduleRepository;

    @InjectMocks private BadgeService badgeService;

    private Badge testBadge;
    private Module testModule;
    private SubBadge testSubBadge;

    @BeforeEach
    void setUp() {
        testBadge = new Badge();
        testBadge.setId(1L);
        testBadge.setName("Game Mastery");
        testBadge.setDescription("Master the game");

        testModule = new Module();
        testModule.setId(1L);
        testModule.setName("Minecraft");

        testSubBadge = new SubBadge();
        testSubBadge.setId(1L);
        testSubBadge.setName("Build a House");
        testSubBadge.setPoints(10);
        testSubBadge.setBadge(testBadge);
        testSubBadge.setModule(testModule);
    }

    // ── Core Badge CRUD ─────────────────────────────────────────────

    @Test
    void findAllBadges_returnsMappedResponses() {
        when(badgeRepository.findAll()).thenReturn(List.of(testBadge));

        List<BadgeResponse> result = badgeService.findAllBadges();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Game Mastery");
    }

    @Test
    void findAllBadges_emptyList() {
        when(badgeRepository.findAll()).thenReturn(List.of());
        assertThat(badgeService.findAllBadges()).isEmpty();
    }

    @Test
    void findBadgeById_success() {
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));

        BadgeResponse response = badgeService.findBadgeById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.description()).isEqualTo("Master the game");
    }

    @Test
    void findBadgeById_notFound_throws() {
        when(badgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> badgeService.findBadgeById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Badge not found");
    }

    @Test
    void createBadge_success() {
        BadgeRequest request = new BadgeRequest("Teamwork", "Work together");
        when(badgeRepository.save(any(Badge.class))).thenAnswer(inv -> {
            Badge b = inv.getArgument(0);
            b.setId(2L);
            return b;
        });

        BadgeResponse response = badgeService.createBadge(request);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("Teamwork");
    }

    @Test
    void updateBadge_success() {
        BadgeRequest request = new BadgeRequest("Updated Badge", "Updated desc");
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));
        when(badgeRepository.save(any(Badge.class))).thenAnswer(inv -> inv.getArgument(0));

        BadgeResponse response = badgeService.updateBadge(1L, request);

        assertThat(response.name()).isEqualTo("Updated Badge");
        assertThat(response.description()).isEqualTo("Updated desc");
    }

    @Test
    void updateBadge_notFound_throws() {
        BadgeRequest request = new BadgeRequest("X", "X");
        when(badgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> badgeService.updateBadge(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Badge not found");
    }

    @Test
    void deleteBadge_success() {
        when(badgeRepository.existsById(1L)).thenReturn(true);

        badgeService.deleteBadge(1L);

        verify(badgeRepository).deleteById(1L);
    }

    @Test
    void deleteBadge_notFound_throws() {
        when(badgeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> badgeService.deleteBadge(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Badge not found");
    }

    // ── Sub-badge CRUD ──────────────────────────────────────────────

    @Test
    void findSubBadgesByBadge_success() {
        when(subBadgeRepository.findByBadgeId(1L)).thenReturn(List.of(testSubBadge));

        List<SubBadgeResponse> result = badgeService.findSubBadgesByBadge(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).badgeName()).isEqualTo("Game Mastery");
    }

    @Test
    void findSubBadgesByModule_success() {
        when(subBadgeRepository.findByModuleId(1L)).thenReturn(List.of(testSubBadge));

        List<SubBadgeResponse> result = badgeService.findSubBadgesByModule(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).moduleName()).isEqualTo("Minecraft");
    }

    @Test
    void findSubBadgeById_success() {
        when(subBadgeRepository.findById(1L)).thenReturn(Optional.of(testSubBadge));

        SubBadgeResponse response = badgeService.findSubBadgeById(1L);

        assertThat(response.name()).isEqualTo("Build a House");
        assertThat(response.points()).isEqualTo(10);
    }

    @Test
    void findSubBadgeById_notFound_throws() {
        when(subBadgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> badgeService.findSubBadgeById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SubBadge not found");
    }

    @Test
    void createSubBadge_success() {
        SubBadgeRequest request = new SubBadgeRequest("Craft a Sword", 5, 1L, 1L);
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(subBadgeRepository.save(any(SubBadge.class))).thenAnswer(inv -> {
            SubBadge s = inv.getArgument(0);
            s.setId(2L);
            return s;
        });

        SubBadgeResponse response = badgeService.createSubBadge(request);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("Craft a Sword");
        assertThat(response.points()).isEqualTo(5);
    }

    @Test
    void createSubBadge_badgeNotFound_throws() {
        SubBadgeRequest request = new SubBadgeRequest("X", 5, 99L, 1L);
        when(badgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> badgeService.createSubBadge(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Badge not found");
    }

    @Test
    void createSubBadge_moduleNotFound_throws() {
        SubBadgeRequest request = new SubBadgeRequest("X", 5, 1L, 99L);
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> badgeService.createSubBadge(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Module not found");
    }

    @Test
    void updateSubBadge_success() {
        SubBadgeRequest request = new SubBadgeRequest("Updated Sub", 20, 1L, 1L);
        when(subBadgeRepository.findById(1L)).thenReturn(Optional.of(testSubBadge));
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(subBadgeRepository.save(any(SubBadge.class))).thenAnswer(inv -> inv.getArgument(0));

        SubBadgeResponse response = badgeService.updateSubBadge(1L, request);

        assertThat(response.name()).isEqualTo("Updated Sub");
        assertThat(response.points()).isEqualTo(20);
    }

    @Test
    void updateSubBadge_notFound_throws() {
        SubBadgeRequest request = new SubBadgeRequest("X", 5, 1L, 1L);
        when(subBadgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> badgeService.updateSubBadge(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SubBadge not found");
    }

    @Test
    void deleteSubBadge_success() {
        when(subBadgeRepository.existsById(1L)).thenReturn(true);

        badgeService.deleteSubBadge(1L);

        verify(subBadgeRepository).deleteById(1L);
    }

    @Test
    void deleteSubBadge_notFound_throws() {
        when(subBadgeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> badgeService.deleteSubBadge(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SubBadge not found");
    }
}

