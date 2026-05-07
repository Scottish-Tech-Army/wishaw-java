package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * A single row in the per-badge XP leaderboard strip.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeLeaderboardEntryDto {
    private int rank;
    private String name;
    private String username;
    private int xp;
}
