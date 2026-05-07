package org.scottishtecharmy.wishaw_java.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.leaderboard.dto.LeaderboardEntry;
import org.scottishtecharmy.wishaw_java.legacy.LegacyPointsRepository;
import org.scottishtecharmy.wishaw_java.progress.UserProgressRepository;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final LegacyPointsRepository legacyPointsRepository;

    public List<LeaderboardEntry> getGlobalLeaderboard() {
        log.debug("Fetching global leaderboard");
        List<Object[]> progressRows = userProgressRepository.findGlobalLeaderboard();
        List<Object[]> legacyRows = legacyPointsRepository.findLegacyTotalsByUser();
        List<LeaderboardEntry> entries = toEntriesCombined(progressRows, legacyRows);
        log.debug("Global leaderboard loaded: {} entries", entries.size());
        return entries;
    }

    public List<LeaderboardEntry> getCentreLeaderboard(Long centreId) {
        log.debug("Fetching leaderboard for centreId={}", centreId);
        List<Object[]> progressRows = userProgressRepository.findLeaderboardByCentre(centreId);
        List<Object[]> legacyRows = legacyPointsRepository.findLegacyTotalsByUserAndCentre(centreId);
        List<LeaderboardEntry> entries = toEntriesCombined(progressRows, legacyRows);
        log.debug("Centre leaderboard loaded: centreId={}, {} entries", centreId, entries.size());
        return entries;
    }

    private List<LeaderboardEntry> toEntriesCombined(List<Object[]> progressRows, List<Object[]> legacyRows) {
        // Build a map of userId -> total XP (progress + legacy)
        Map<Long, Integer> xpMap = new HashMap<>();
        for (Object[] row : progressRows) {
            Long userId = (Long) row[0];
            int xp = ((Number) row[1]).intValue();
            xpMap.merge(userId, xp, Integer::sum);
        }
        for (Object[] row : legacyRows) {
            Long userId = (Long) row[0];
            int xp = ((Number) row[1]).intValue();
            xpMap.merge(userId, xp, Integer::sum);
        }

        // Sort by total XP descending
        List<Map.Entry<Long, Integer>> sorted = new ArrayList<>(xpMap.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<Long, Integer> entry : sorted) {
            Long userId = entry.getKey();
            int totalXp = entry.getValue();
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                entries.add(new LeaderboardEntry(
                        rank++,
                        user.getId(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getCentre() != null ? user.getCentre().getName() : null,
                        user.getDob(),
                        totalXp
                ));
            } else {
                log.warn("Leaderboard skipping deleted user: userId={}", userId);
            }
        }
        return entries;
    }
}
