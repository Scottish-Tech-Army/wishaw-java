package org.scottishtecharmy.wishaw_java.leaderboard.dto;

import java.time.LocalDate;

public record LeaderboardEntry(
        int rank,
        Long userId,
        String username,
        String displayName,
        String centreName,
        LocalDate dob,
        int totalXp
) { }
