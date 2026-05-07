package org.scottishtecharmy.wishaw_java.dto;

import java.util.Map;

public final class LeaderboardDtos {

    private LeaderboardDtos() {
    }

    public record LeaderboardEntryDto(
            String userId,
            String displayName,
            String centreId,
            String centreName,
            int totalPoints,
            Map<String, String> badgeLevels,
            int completedModules
    ) {
    }

    public record LtcBadgeDto(String id, String name, String icon, String description) {
    }

    public record CreateBadgeRequest(String name, String icon, String description) {
    }

    public record BadgeAssignRequest(String badgeId, String userId) {
    }

    public record EarnedBadgeDto(String id, String name, String icon, String earnedAt) {
    }

    public record CaloriesLogRequest(String userId, String sportName, Integer calories) {
    }

    public record CaloriesSummaryDto(int total, Map<String, Integer> bySport) {
    }
}
