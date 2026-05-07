package org.scottishtecharmy.wishaw_java.progress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.badge.Badge;
import org.scottishtecharmy.wishaw_java.badge.BadgeRepository;
import org.scottishtecharmy.wishaw_java.badge.SubBadge;
import org.scottishtecharmy.wishaw_java.badge.SubBadgeRepository;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.level.LevelService;
import org.scottishtecharmy.wishaw_java.legacy.LegacyPointsRepository;
import org.scottishtecharmy.wishaw_java.module.Module;
import org.scottishtecharmy.wishaw_java.progress.dto.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.progress.dto.UserProfileResponse;
import org.scottishtecharmy.wishaw_java.user.Role;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock private LevelService levelService;
    @Mock private UserProgressRepository userProgressRepository;
    @Mock private SubBadgeCompletionRepository subBadgeCompletionRepository;
    @Mock private UserRepository userRepository;
    @Mock private SubBadgeRepository subBadgeRepository;
    @Mock private BadgeRepository badgeRepository;
    @Mock private LegacyPointsRepository legacyPointsRepository;

    @InjectMocks private ProgressService progressService;

    private User testUser;
    private Badge testBadge;
    private SubBadge testSubBadge;
    private Module testModule;
    private UserProgress testProgress;
    private Centre testCentre;

    @BeforeEach
    void setUp() {
        testCentre = new Centre();
        testCentre.setId(1L);
        testCentre.setName("Wishaw YMCA");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john");
        testUser.setDisplayName("John Doe");
        testUser.setRole(Role.USER);
        testUser.setCentre(testCentre);

        testBadge = new Badge();
        testBadge.setId(1L);
        testBadge.setName("Game Mastery");

        testModule = new Module();
        testModule.setId(1L);
        testModule.setName("Minecraft");

        testSubBadge = new SubBadge();
        testSubBadge.setId(1L);
        testSubBadge.setName("Build a House");
        testSubBadge.setPoints(10);
        testSubBadge.setBadge(testBadge);
        testSubBadge.setModule(testModule);

        testProgress = new UserProgress();
        testProgress.setId(1L);
        testProgress.setUser(testUser);
        testProgress.setBadge(testBadge);
        testProgress.setTotalPoints(0);
    }

    // ── completeSubBadge ────────────────────────────────────────────

    @Test
    void completeSubBadge_success_addsPointsAndRecordsCompletion() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subBadgeRepository.findById(1L)).thenReturn(Optional.of(testSubBadge));
        when(subBadgeCompletionRepository.existsByUserIdAndSubBadgeId(1L, 1L)).thenReturn(false);
        when(subBadgeCompletionRepository.save(any(SubBadgeCompletion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userProgressRepository.findByUserAndBadge(testUser, testBadge)).thenReturn(Optional.of(testProgress));
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(levelService.calculateLevel(10)).thenReturn("BRONZE");

        SubBadgeCompletion completion = new SubBadgeCompletion();
        completion.setUser(testUser);
        completion.setSubBadge(testSubBadge);
        when(subBadgeCompletionRepository.findByUserIdAndSubBadgeBadgeId(1L, 1L))
                .thenReturn(List.of(completion));

        BadgeProgressResponse response = progressService.completeSubBadge(1L, 1L);

        assertThat(response.badgeId()).isEqualTo(1L);
        assertThat(response.totalPoints()).isEqualTo(10);
        assertThat(response.level()).isEqualTo("BRONZE");
        assertThat(response.earnedSubBadges()).hasSize(1);
        assertThat(response.earnedSubBadges().get(0).name()).isEqualTo("Build a House");

        verify(subBadgeCompletionRepository).save(any(SubBadgeCompletion.class));
        verify(userProgressRepository).save(argThat(p -> p.getTotalPoints() == 10));
    }

    @Test
    void completeSubBadge_createsNewProgress_whenNoneExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subBadgeRepository.findById(1L)).thenReturn(Optional.of(testSubBadge));
        when(subBadgeCompletionRepository.existsByUserIdAndSubBadgeId(1L, 1L)).thenReturn(false);
        when(subBadgeCompletionRepository.save(any(SubBadgeCompletion.class))).thenAnswer(inv -> inv.getArgument(0));

        // No existing progress → findOrCreateProgress creates new one
        UserProgress newProgress = new UserProgress();
        newProgress.setUser(testUser);
        newProgress.setBadge(testBadge);
        newProgress.setTotalPoints(0);

        when(userProgressRepository.findByUserAndBadge(testUser, testBadge)).thenReturn(Optional.empty());
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(inv -> {
            UserProgress p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(levelService.calculateLevel(anyInt())).thenReturn("BRONZE");
        when(subBadgeCompletionRepository.findByUserIdAndSubBadgeBadgeId(1L, 1L)).thenReturn(List.of());

        BadgeProgressResponse response = progressService.completeSubBadge(1L, 1L);

        assertThat(response.totalPoints()).isEqualTo(10);
        // save called twice: once for findOrCreate, once after adding points
        verify(userProgressRepository, atLeast(2)).save(any(UserProgress.class));
    }

    @Test
    void completeSubBadge_duplicateCompletion_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subBadgeRepository.findById(1L)).thenReturn(Optional.of(testSubBadge));
        when(subBadgeCompletionRepository.existsByUserIdAndSubBadgeId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> progressService.completeSubBadge(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sub-badge already completed by this user");

        verify(subBadgeCompletionRepository, never()).save(any());
    }

    @Test
    void completeSubBadge_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.completeSubBadge(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void completeSubBadge_subBadgeNotFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subBadgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.completeSubBadge(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SubBadge not found");
    }

    // ── getUserProfile ──────────────────────────────────────────────

    @Test
    void getUserProfile_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findAll()).thenReturn(List.of(testBadge));
        when(userProgressRepository.findByUserAndBadge(testUser, testBadge))
                .thenReturn(Optional.of(testProgress));
        when(levelService.calculateLevel(0)).thenReturn("UNRANKED");
        when(subBadgeCompletionRepository.findByUserIdAndSubBadgeBadgeId(1L, 1L))
                .thenReturn(List.of());
        when(subBadgeCompletionRepository.countByUserId(1L)).thenReturn(0L);

        UserProfileResponse response = progressService.getUserProfile(1L);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.displayName()).isEqualTo("John Doe");
        assertThat(response.centreName()).isEqualTo("Wishaw YMCA");
        assertThat(response.badges()).hasSize(1);
        assertThat(response.overallXp()).isEqualTo(0);
        assertThat(response.completedSubBadges()).isEqualTo(0L);
    }

    @Test
    void getUserProfile_withEarnedSubBadges() {
        testProgress.setTotalPoints(10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findAll()).thenReturn(List.of(testBadge));
        when(userProgressRepository.findByUserAndBadge(testUser, testBadge))
                .thenReturn(Optional.of(testProgress));
        when(levelService.calculateLevel(10)).thenReturn("BRONZE");

        SubBadgeCompletion completion = new SubBadgeCompletion();
        completion.setUser(testUser);
        completion.setSubBadge(testSubBadge);
        when(subBadgeCompletionRepository.findByUserIdAndSubBadgeBadgeId(1L, 1L))
                .thenReturn(List.of(completion));
        when(subBadgeCompletionRepository.countByUserId(1L)).thenReturn(1L);

        UserProfileResponse response = progressService.getUserProfile(1L);

        assertThat(response.overallXp()).isEqualTo(10);
        assertThat(response.completedSubBadges()).isEqualTo(1L);
        assertThat(response.badges().get(0).earnedSubBadges()).hasSize(1);
        assertThat(response.badges().get(0).level()).isEqualTo("BRONZE");
    }

    @Test
    void getUserProfile_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.getUserProfile(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUserProfile_withNullCentre_returnsNull() {
        testUser.setCentre(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findAll()).thenReturn(List.of());
        when(subBadgeCompletionRepository.countByUserId(1L)).thenReturn(0L);

        UserProfileResponse response = progressService.getUserProfile(1L);

        assertThat(response.centreName()).isNull();
    }

    // ── getBadgeProgress ────────────────────────────────────────────

    @Test
    void getBadgeProgress_success() {
        testProgress.setTotalPoints(50);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));
        when(userProgressRepository.findByUserAndBadge(testUser, testBadge))
                .thenReturn(Optional.of(testProgress));
        when(levelService.calculateLevel(50)).thenReturn("BRONZE");
        when(subBadgeCompletionRepository.findByUserIdAndSubBadgeBadgeId(1L, 1L))
                .thenReturn(List.of());

        BadgeProgressResponse response = progressService.getBadgeProgress(1L, 1L);

        assertThat(response.badgeId()).isEqualTo(1L);
        assertThat(response.badgeName()).isEqualTo("Game Mastery");
        assertThat(response.totalPoints()).isEqualTo(50);
        assertThat(response.level()).isEqualTo("BRONZE");
    }

    @Test
    void getBadgeProgress_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.getBadgeProgress(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getBadgeProgress_badgeNotFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.getBadgeProgress(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Badge not found");
    }

    @Test
    void getBadgeProgress_createsNewProgress_whenNoneExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(testBadge));
        when(userProgressRepository.findByUserAndBadge(testUser, testBadge))
                .thenReturn(Optional.empty());
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(inv -> {
            UserProgress p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(levelService.calculateLevel(0)).thenReturn("UNRANKED");
        when(subBadgeCompletionRepository.findByUserIdAndSubBadgeBadgeId(1L, 1L))
                .thenReturn(List.of());

        BadgeProgressResponse response = progressService.getBadgeProgress(1L, 1L);

        assertThat(response.totalPoints()).isEqualTo(0);
        assertThat(response.level()).isEqualTo("UNRANKED");
        verify(userProgressRepository).save(any(UserProgress.class));
    }
}
