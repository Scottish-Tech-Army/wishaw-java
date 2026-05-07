package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardCentreDto {
    private int rank;
    private String name;
    private String icon;
    private int memberCount;
    private int periodXp;
    private int totalBadges;
    private int totalModules;
    private String topPlayerName;
}
