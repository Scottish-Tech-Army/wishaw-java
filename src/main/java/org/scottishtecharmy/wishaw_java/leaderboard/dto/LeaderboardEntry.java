package org.scottishtecharmy.wishaw_java.leaderboard.dto;

public record LeaderboardEntry(
        int rank,
        Long userId,
        String username,
        String displayName,
        String centreName,
        int totalXp
) { }

