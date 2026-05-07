package org.scottishtecharmy.wishaw_java.service.leaderboard;

import org.scottishtecharmy.wishaw_java.dto.response.LeaderboardEntryResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeLevel;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.repository.BadgeLevelRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeProgressRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardService {

    private final UserAccountRepository userAccountRepository;
    private final PlayerBadgeProgressRepository playerBadgeProgressRepository;
    private final BadgeLevelRepository badgeLevelRepository;

    public List<LeaderboardEntryResponse> getGlobalLeaderboard() {
        return buildLeaderboard(player -> true);
    }

    public List<LeaderboardEntryResponse> getCentreLeaderboard(Long centreId) {
        return buildLeaderboard(player -> player.getCentre() != null && centreId.equals(player.getCentre().getId()));
    }

    public List<LeaderboardEntryResponse> getGroupLeaderboard(Long groupId) {
        return buildLeaderboard(player -> player.getGroup() != null && groupId.equals(player.getGroup().getId()));
    }

    private List<LeaderboardEntryResponse> buildLeaderboard(Predicate<UserAccount> predicate) {
        Map<String, Integer> levelRank = badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc().stream()
                .collect(Collectors.toMap(BadgeLevel::getName, BadgeLevel::getRankOrder));

        List<LeaderboardRow> rows = userAccountRepository.findByRole(Role.PLAYER).stream()
                .filter(UserAccount::isActive)
                .filter(predicate)
                .map(player -> toRow(player, levelRank))
                .sorted(Comparator.comparingInt(LeaderboardRow::totalPoints).reversed()
                        .thenComparing(LeaderboardRow::displayName))
                .toList();

        List<LeaderboardEntryResponse> response = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            LeaderboardRow row = rows.get(index);
            response.add(LeaderboardEntryResponse.builder()
                    .rank(index + 1)
                    .playerId(row.playerId())
                    .displayName(row.displayName())
                    .centreName(row.centreName())
                    .groupName(row.groupName())
                    .totalPoints(row.totalPoints())
                    .highestLevel(row.highestLevel())
                    .build());
        }
        return response;
    }

    private LeaderboardRow toRow(UserAccount player, Map<String, Integer> levelRank) {
        List<PlayerBadgeProgress> progressList = playerBadgeProgressRepository.findByPlayerId(player.getId());
        int totalPoints = progressList.stream().mapToInt(PlayerBadgeProgress::getTotalPoints).sum();
        String highestLevel = progressList.stream()
                .map(PlayerBadgeProgress::getCurrentLevelName)
                .filter(levelRank::containsKey)
                .max(Comparator.comparingInt(levelRank::get))
                .orElse(null);

        return new LeaderboardRow(
                player.getId(),
                player.getDisplayName(),
                player.getCentre() != null ? player.getCentre().getName() : null,
                player.getGroup() != null ? player.getGroup().getName() : null,
                totalPoints,
                highestLevel
        );
    }

    private record LeaderboardRow(
            Long playerId,
            String displayName,
            String centreName,
            String groupName,
            int totalPoints,
            String highestLevel
    ) {
    }
}