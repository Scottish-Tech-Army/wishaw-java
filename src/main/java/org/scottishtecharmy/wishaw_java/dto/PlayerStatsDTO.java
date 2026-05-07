package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerStatsDTO {
    private Long playerId;
    private String playerName;
    private Integer totalMatches;
    private Integer wins;
    private Integer losses;
    private Integer draws;
    private Double totalCalories;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Long excusedCount;
    private Double attendancePercentage;
    private List<PlayerBadgeDTO> badges;
    private List<RankingDTO> rankings;
}

