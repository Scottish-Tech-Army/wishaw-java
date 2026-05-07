package org.scottishtecharmy.wishaw_java.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.leaderboard.dto.LeaderboardEntry;
import org.scottishtecharmy.wishaw_java.legacy.LegacyPointsRepository;
import org.scottishtecharmy.wishaw_java.progress.UserProgressRepository;
import org.scottishtecharmy.wishaw_java.user.Role;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock private UserProgressRepository userProgressRepository;
    @Mock private UserRepository userRepository;
    @Mock private LegacyPointsRepository legacyPointsRepository;

    @InjectMocks private LeaderboardService leaderboardService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        Centre centre = new Centre();
        centre.setId(1L);
        centre.setName("Wishaw YMCA");

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");
        user1.setDisplayName("Alice");
        user1.setRole(Role.USER);
        user1.setCentre(centre);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");
        user2.setDisplayName("Bob");
        user2.setRole(Role.USER);
        user2.setCentre(centre);
    }

    // ── getGlobalLeaderboard ────────────────────────────────────────

    @Test
    void getGlobalLeaderboard_returnsRankedEntries() {
        List<Object[]> rows = List.of(
                new Object[]{1L, 250},
                new Object[]{2L, 100}
        );
        when(userProgressRepository.findGlobalLeaderboard()).thenReturn(rows);
        when(legacyPointsRepository.findLegacyTotalsByUser()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        List<LeaderboardEntry> result = leaderboardService.getGlobalLeaderboard();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).username()).isEqualTo("alice");
        assertThat(result.get(0).totalXp()).isEqualTo(250);
        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(1).username()).isEqualTo("bob");
        assertThat(result.get(1).totalXp()).isEqualTo(100);
    }

    @Test
    void getGlobalLeaderboard_emptyList() {
        when(userProgressRepository.findGlobalLeaderboard()).thenReturn(List.of());
        when(legacyPointsRepository.findLegacyTotalsByUser()).thenReturn(List.of());

        List<LeaderboardEntry> result = leaderboardService.getGlobalLeaderboard();

        assertThat(result).isEmpty();
    }

    @Test
    void getGlobalLeaderboard_skipsDeletedUsers() {
        List<Object[]> rows = List.of(
                new Object[]{1L, 250},
                new Object[]{999L, 100}  // user no longer exists
        );
        when(userProgressRepository.findGlobalLeaderboard()).thenReturn(rows);
        when(legacyPointsRepository.findLegacyTotalsByUser()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        List<LeaderboardEntry> result = leaderboardService.getGlobalLeaderboard();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("alice");
    }

    // ── getCentreLeaderboard ────────────────────────────────────────

    @Test
    void getCentreLeaderboard_returnsFilteredEntries() {
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{1L, 150});
        when(userProgressRepository.findLeaderboardByCentre(1L)).thenReturn(rows);
        when(legacyPointsRepository.findLegacyTotalsByUserAndCentre(1L)).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        List<LeaderboardEntry> result = leaderboardService.getCentreLeaderboard(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).centreName()).isEqualTo("Wishaw YMCA");
        assertThat(result.get(0).totalXp()).isEqualTo(150);
    }

    @Test
    void getCentreLeaderboard_emptyList() {
        when(userProgressRepository.findLeaderboardByCentre(99L)).thenReturn(List.of());
        when(legacyPointsRepository.findLegacyTotalsByUserAndCentre(99L)).thenReturn(List.of());

        assertThat(leaderboardService.getCentreLeaderboard(99L)).isEmpty();
    }

    // ── ranking is sequential ───────────────────────────────────────

    @Test
    void leaderboard_rankingIsSequential() {
        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("charlie");
        user3.setDisplayName("Charlie");
        user3.setRole(Role.USER);

        List<Object[]> rows = List.of(
                new Object[]{1L, 500},
                new Object[]{2L, 300},
                new Object[]{3L, 100}
        );
        when(userProgressRepository.findGlobalLeaderboard()).thenReturn(rows);
        when(legacyPointsRepository.findLegacyTotalsByUser()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        List<LeaderboardEntry> result = leaderboardService.getGlobalLeaderboard();

        assertThat(result).extracting(LeaderboardEntry::rank).containsExactly(1, 2, 3);
    }

    // ── user with null centre ───────────────────────────────────────

    @Test
    void leaderboard_userWithNullCentre_returnsNullCentreName() {
        user1.setCentre(null);
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{1L, 100});
        when(userProgressRepository.findGlobalLeaderboard()).thenReturn(rows);
        when(legacyPointsRepository.findLegacyTotalsByUser()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        List<LeaderboardEntry> result = leaderboardService.getGlobalLeaderboard();

        assertThat(result.get(0).centreName()).isNull();
    }
}
