package org.scottishtecharmy.wishaw_java.service.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.dto.request.AwardChallengeRequest;
import org.scottishtecharmy.wishaw_java.dto.request.SetLegacyPointsRequest;
import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.BadgeLevel;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.ChallengeAward;
import org.scottishtecharmy.wishaw_java.entity.Module;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.enums.SourceType;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.BadgeLevelRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeAwardRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeProgressRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private BadgeCategoryRepository badgeCategoryRepository;

    @Mock
    private BadgeLevelRepository badgeLevelRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeAwardRepository challengeAwardRepository;

    @Mock
    private PlayerBadgeProgressRepository playerBadgeProgressRepository;

    @InjectMocks
    private ProgressService progressService;

    @BeforeEach
    void setUp() {
        lenient().when(playerBadgeProgressRepository.save(any(PlayerBadgeProgress.class)))
                .thenAnswer(invocation -> {
                    PlayerBadgeProgress progress = invocation.getArgument(0);
                    progress.setTotalPoints(progress.getLegacyPoints() + progress.getEarnedPoints());
                    return progress;
                });
        lenient().when(playerBadgeProgressRepository.saveAndFlush(any(PlayerBadgeProgress.class)))
                .thenAnswer(invocation -> {
                    PlayerBadgeProgress progress = invocation.getArgument(0);
                    progress.setTotalPoints(progress.getLegacyPoints() + progress.getEarnedPoints());
                    return progress;
                });
        lenient().when(challengeAwardRepository.save(any(ChallengeAward.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void setLegacyPointsUpdatesProgressAndReturnsRecalculatedTotals() {
        UserAccount player = player(10L, "player1", null);
        BadgeCategory category = category(20L, "TEAMWORK", "Teamwork", true);
        PlayerBadgeProgress progress = progress(player, category, 0, 0, null);

        when(userAccountRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(badgeCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(playerBadgeProgressRepository.findByPlayerIdAndBadgeCategoryId(player.getId(), category.getId()))
                .thenReturn(Optional.of(progress));
        when(badgeCategoryRepository.findAll()).thenReturn(List.of(category));
        when(challengeAwardRepository.sumPointsByPlayerAndCategory(player.getId(), category.getId())).thenReturn(5);
        when(badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc())
                .thenReturn(List.of(level("Bronze", 0, 30, 1), level("Silver", 31, 70, 2)));

        SetLegacyPointsRequest request = new SetLegacyPointsRequest();
        request.setPlayerId(player.getId());
        request.setBadgeCategoryId(category.getId());
        request.setLegacyPoints(35);

        BadgeProgressResponse response = progressService.setLegacyPoints(request);

        assertThat(response.getLegacyPoints()).isEqualTo(35);
        assertThat(response.getEarnedPoints()).isEqualTo(5);
        assertThat(response.getTotalPoints()).isEqualTo(40);
        assertThat(response.getCurrentLevel()).isEqualTo("Silver");
    }

    @Test
    void awardChallengeCreatesManualAwardAndReturnsUpdatedProgress() {
        Centre centre = centre(1L, "WISHAW");
        UserAccount player = player(10L, "player1", centre);
        UserAccount admin = admin(30L, "superadmin", Role.SUPER_ADMIN, centre);
        BadgeCategory category = category(20L, "GAME_MASTERY", "Game Mastery", true);
        Challenge challenge = challenge(40L, category, 15);
        PlayerBadgeProgress progress = progress(player, category, 0, 0, null);

        when(userAccountRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        when(userAccountRepository.findByUsername(admin.getUsername())).thenReturn(Optional.of(admin));
        when(playerBadgeProgressRepository.findByPlayerIdAndBadgeCategoryId(player.getId(), category.getId()))
                .thenReturn(Optional.of(progress));
        when(badgeCategoryRepository.findAll()).thenReturn(List.of(category));
        when(challengeAwardRepository.sumPointsByPlayerAndCategory(player.getId(), category.getId())).thenReturn(15);
        when(badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc())
                .thenReturn(List.of(level("Bronze", 0, 30, 1), level("Silver", 31, 70, 2)));

        AwardChallengeRequest request = new AwardChallengeRequest();
        request.setPlayerId(player.getId());
        request.setChallengeId(challenge.getId());
        request.setNotes("Award for match review");

        BadgeProgressResponse response = progressService.awardChallenge(request, admin.getUsername());

        assertThat(response.getCategoryCode()).isEqualTo("GAME_MASTERY");
        assertThat(response.getEarnedPoints()).isEqualTo(15);
        assertThat(response.getTotalPoints()).isEqualTo(15);
        verify(challengeAwardRepository).save(any(ChallengeAward.class));
    }

    @Test
    void awardChallengeRejectsCentreAdminForPlayerInAnotherCentre() {
        Centre adminCentre = centre(1L, "WISHAW");
        Centre otherCentre = centre(2L, "OTHER");
        UserAccount player = player(10L, "player1", otherCentre);
        UserAccount admin = admin(30L, "centreadmin", Role.CENTRE_ADMIN, adminCentre);
        BadgeCategory category = category(20L, "GAME_MASTERY", "Game Mastery", true);
        Challenge challenge = challenge(40L, category, 15);

        when(userAccountRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        when(userAccountRepository.findByUsername(admin.getUsername())).thenReturn(Optional.of(admin));

        AwardChallengeRequest request = new AwardChallengeRequest();
        request.setPlayerId(player.getId());
        request.setChallengeId(challenge.getId());

        assertThatThrownBy(() -> progressService.awardChallenge(request, admin.getUsername()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Centre admin cannot access players outside their centre");

        verify(challengeAwardRepository, never()).save(any(ChallengeAward.class));
        verifyNoInteractions(playerBadgeProgressRepository, badgeCategoryRepository, badgeLevelRepository);
    }

    @Test
    void recalculatePlayerProgressIgnoresInactiveCategoriesAndAppliesLevelThresholds() {
        UserAccount player = player(10L, "player1", null);
        BadgeCategory activeCategory = category(20L, "DIGITAL_SKILLS", "Digital Skills", true);
        BadgeCategory inactiveCategory = category(21L, "OLD", "Old", false);
        PlayerBadgeProgress progress = progress(player, activeCategory, 10, 0, null);

        when(userAccountRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(badgeCategoryRepository.findAll()).thenReturn(List.of(activeCategory, inactiveCategory));
        when(playerBadgeProgressRepository.findByPlayerIdAndBadgeCategoryId(player.getId(), activeCategory.getId()))
                .thenReturn(Optional.of(progress));
        when(challengeAwardRepository.sumPointsByPlayerAndCategory(player.getId(), activeCategory.getId())).thenReturn(72);
        when(badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc())
                .thenReturn(List.of(
                        level("Bronze", 0, 30, 1),
                        level("Silver", 31, 70, 2),
                        level("Gold", 71, 120, 3)
                ));

        progressService.recalculatePlayerProgress(player.getId());

        assertThat(progress.getEarnedPoints()).isEqualTo(72);
        assertThat(progress.getTotalPoints()).isEqualTo(82);
        assertThat(progress.getCurrentLevelName()).isEqualTo("Gold");
        verify(challengeAwardRepository).sumPointsByPlayerAndCategory(player.getId(), activeCategory.getId());
        verify(playerBadgeProgressRepository).save(progress);
    }

    @Test
    void setLegacyPointsRejectsNonPlayerAccounts() {
        UserAccount admin = admin(99L, "admin1", Role.SUPER_ADMIN, null);
        SetLegacyPointsRequest request = new SetLegacyPointsRequest();
        request.setPlayerId(admin.getId());
        request.setBadgeCategoryId(1L);
        request.setLegacyPoints(10);

        when(userAccountRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> progressService.setLegacyPoints(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User is not a player");

        verifyNoInteractions(badgeCategoryRepository, playerBadgeProgressRepository, challengeAwardRepository);
    }

    @Test
    void setLegacyPointsRecoversWhenProgressRowIsCreatedConcurrently() {
        UserAccount player = player(10L, "player1", null);
        BadgeCategory category = category(20L, "TEAMWORK", "Teamwork", true);
        PlayerBadgeProgress existingProgress = progress(player, category, 0, 0, "Bronze");

        when(userAccountRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(badgeCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(playerBadgeProgressRepository.findByPlayerIdAndBadgeCategoryId(player.getId(), category.getId()))
                .thenReturn(Optional.empty(), Optional.of(existingProgress), Optional.of(existingProgress), Optional.of(existingProgress));
        when(playerBadgeProgressRepository.saveAndFlush(any(PlayerBadgeProgress.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));
        when(badgeCategoryRepository.findAll()).thenReturn(List.of(category));
        when(challengeAwardRepository.sumPointsByPlayerAndCategory(player.getId(), category.getId())).thenReturn(0);
        when(badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc())
                .thenReturn(List.of(level("Bronze", 0, 30, 1), level("Silver", 31, 70, 2)));

        SetLegacyPointsRequest request = new SetLegacyPointsRequest();
        request.setPlayerId(player.getId());
        request.setBadgeCategoryId(category.getId());
        request.setLegacyPoints(10);

        BadgeProgressResponse response = progressService.setLegacyPoints(request);

        assertThat(response.getLegacyPoints()).isEqualTo(10);
        assertThat(response.getTotalPoints()).isEqualTo(10);
        assertThat(response.getCurrentLevel()).isEqualTo("Bronze");
        verify(playerBadgeProgressRepository).saveAndFlush(any(PlayerBadgeProgress.class));
        verify(playerBadgeProgressRepository, times(2)).save(existingProgress);
    }

    private UserAccount player(Long id, String username, Centre centre) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setUsername(username);
        user.setDisplayName("Player " + username);
        user.setRole(Role.PLAYER);
        user.setCentre(centre);
        return user;
    }

    private UserAccount admin(Long id, String username, Role role, Centre centre) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setUsername(username);
        user.setDisplayName("Admin " + username);
        user.setRole(role);
        user.setCentre(centre);
        return user;
    }

    private Centre centre(Long id, String code) {
        Centre centre = new Centre();
        centre.setId(id);
        centre.setName(code + " Centre");
        centre.setCode(code);
        return centre;
    }

    private BadgeCategory category(Long id, String code, String name, boolean active) {
        BadgeCategory category = new BadgeCategory();
        category.setId(id);
        category.setCode(code);
        category.setDisplayName(name);
        category.setActive(active);
        return category;
    }

    private BadgeLevel level(String name, int minPoints, Integer maxPoints, int rankOrder) {
        BadgeLevel level = new BadgeLevel();
        level.setName(name);
        level.setMinPoints(minPoints);
        level.setMaxPoints(maxPoints);
        level.setRankOrder(rankOrder);
        return level;
    }

    private PlayerBadgeProgress progress(UserAccount player, BadgeCategory category, int legacy, int earned, String levelName) {
        PlayerBadgeProgress progress = new PlayerBadgeProgress();
        progress.setPlayer(player);
        progress.setBadgeCategory(category);
        progress.setLegacyPoints(legacy);
        progress.setEarnedPoints(earned);
        progress.setTotalPoints(legacy + earned);
        progress.setCurrentLevelName(levelName);
        return progress;
    }

    private Challenge challenge(Long id, BadgeCategory category, int points) {
        Challenge challenge = new Challenge();
        challenge.setId(id);
        challenge.setBadgeCategory(category);
        challenge.setPoints(points);
        Module module = new Module();
        module.setId(200L);
        module.setName("Module");
        challenge.setModule(module);
        return challenge;
    }
}
