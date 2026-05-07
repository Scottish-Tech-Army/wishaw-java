package org.scottishtecharmy.wishaw_java.service.leaderboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.dto.response.LeaderboardEntryResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeLevel;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.Group;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.repository.BadgeLevelRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeProgressRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PlayerBadgeProgressRepository playerBadgeProgressRepository;

    @Mock
    private BadgeLevelRepository badgeLevelRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void globalLeaderboardSortsByPointsThenDisplayName() {
        UserAccount amy = player(1L, "Amy", centre(10L, "Wishaw"), group(20L, "RL"), true);
        UserAccount ben = player(2L, "Ben", centre(10L, "Wishaw"), group(20L, "RL"), true);
        UserAccount zane = player(3L, "Zane", centre(11L, "Glasgow"), group(21L, "FN"), true);
        UserAccount inactive = player(4L, "Inactive", centre(11L, "Glasgow"), group(21L, "FN"), false);

        when(userAccountRepository.findByRole(Role.PLAYER)).thenReturn(List.of(amy, ben, zane, inactive));
        when(badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc()).thenReturn(List.of(
                level("Bronze", 1),
                level("Silver", 2),
                level("Gold", 3)
        ));
        when(playerBadgeProgressRepository.findByPlayerId(amy.getId())).thenReturn(List.of(
                progress(30, "Silver"),
                progress(15, "Bronze")
        ));
        when(playerBadgeProgressRepository.findByPlayerId(ben.getId())).thenReturn(List.of(
                progress(45, "Bronze")
        ));
        when(playerBadgeProgressRepository.findByPlayerId(zane.getId())).thenReturn(List.of(
                progress(10, "Bronze")
        ));

        List<LeaderboardEntryResponse> entries = leaderboardService.getGlobalLeaderboard();

        assertThat(entries).hasSize(3);
        assertThat(entries.get(0).getDisplayName()).isEqualTo("Amy");
        assertThat(entries.get(0).getRank()).isEqualTo(1);
        assertThat(entries.get(0).getTotalPoints()).isEqualTo(45);
        assertThat(entries.get(0).getHighestLevel()).isEqualTo("Silver");
        assertThat(entries.get(1).getDisplayName()).isEqualTo("Ben");
        assertThat(entries.get(1).getRank()).isEqualTo(2);
        assertThat(entries.get(2).getDisplayName()).isEqualTo("Zane");
    }

    @Test
    void centreLeaderboardFiltersPlayersToSelectedCentre() {
        Centre wishaw = centre(10L, "Wishaw");
        Centre glasgow = centre(11L, "Glasgow");
        UserAccount wishawPlayer = player(1L, "Player A", wishaw, group(20L, "RL"), true);
        UserAccount glasgowPlayer = player(2L, "Player B", glasgow, group(21L, "FN"), true);

        when(userAccountRepository.findByRole(Role.PLAYER)).thenReturn(List.of(wishawPlayer, glasgowPlayer));
        when(badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc()).thenReturn(List.of(level("Bronze", 1)));
        when(playerBadgeProgressRepository.findByPlayerId(wishawPlayer.getId())).thenReturn(List.of(progress(10, "Bronze")));

        List<LeaderboardEntryResponse> entries = leaderboardService.getCentreLeaderboard(wishaw.getId());

        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getPlayerId()).isEqualTo(wishawPlayer.getId());
        assertThat(entries.get(0).getCentreName()).isEqualTo("Wishaw");
    }

    private UserAccount player(Long id, String displayName, Centre centre, Group group, boolean active) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setDisplayName(displayName);
        user.setRole(Role.PLAYER);
        user.setCentre(centre);
        user.setGroup(group);
        user.setActive(active);
        return user;
    }

    private Centre centre(Long id, String name) {
        Centre centre = new Centre();
        centre.setId(id);
        centre.setName(name);
        return centre;
    }

    private Group group(Long id, String name) {
        Group group = new Group();
        group.setId(id);
        group.setName(name);
        return group;
    }

    private BadgeLevel level(String name, int rank) {
        BadgeLevel level = new BadgeLevel();
        level.setName(name);
        level.setRankOrder(rank);
        return level;
    }

    private PlayerBadgeProgress progress(int totalPoints, String levelName) {
        PlayerBadgeProgress progress = new PlayerBadgeProgress();
        progress.setTotalPoints(totalPoints);
        progress.setCurrentLevelName(levelName);
        return progress;
    }
}