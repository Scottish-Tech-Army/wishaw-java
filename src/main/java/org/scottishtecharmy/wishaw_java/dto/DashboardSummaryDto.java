package org.scottishtecharmy.wishaw_java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDto {
    private long studentId;
    private String gamertag;
    private String name;
    private String username;
    private String avatarUrl;
    private String bio;
    private String joinedDate;
    private String hub;

    private int level;
    private int xp;
    private int xpForNextLevel;

    private int weeklyXp;
    private int teamWeeklyXp;
    private int hubWeeklyXp;

    private int totalSubBadges;
    private int earnedSubBadges;

    private Integer leaderboardRank;
    private String nextSessionAt;

    // Team information
    private String teamName;
    private String teamIcon;
    private String teamId;
    private String teamColour;

    @JsonProperty("isCaptain")
    private boolean isCaptain;

    private List<MainBadgeSummaryDto> badges;
    private List<XpEventDto> recentActivity;
}
