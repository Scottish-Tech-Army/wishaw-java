package org.scottishtecharmy.wishaw_java.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.leaderboard.dto.LeaderboardEntry;
import org.scottishtecharmy.wishaw_java.progress.UserProgressRepository;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;

    public List<LeaderboardEntry> getGlobalLeaderboard() {
        log.debug("Fetching global leaderboard");
        List<Object[]> rows = userProgressRepository.findGlobalLeaderboard();
        List<LeaderboardEntry> entries = toEntries(rows);
        log.debug("Global leaderboard loaded: {} entries", entries.size());
        return entries;
    }

    public List<LeaderboardEntry> getCentreLeaderboard(Long centreId) {
        log.debug("Fetching leaderboard for centreId={}", centreId);
        List<Object[]> rows = userProgressRepository.findLeaderboardByCentre(centreId);
        List<LeaderboardEntry> entries = toEntries(rows);
        log.debug("Centre leaderboard loaded: centreId={}, {} entries", centreId, entries.size());
        return entries;
    }

    private List<LeaderboardEntry> toEntries(List<Object[]> rows) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;
        for (Object[] row : rows) {
            Long userId = (Long) row[0];
            int totalXp = ((Number) row[1]).intValue();
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                entries.add(new LeaderboardEntry(
                        rank++,
                        user.getId(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getCentre() != null ? user.getCentre().getName() : null,
                        totalXp
                ));
            } else {
                log.warn("Leaderboard skipping deleted user: userId={}", userId);
            }
        }
        return entries;
    }
}
