package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberBadgeProgressDto {
    private String mainBadgeId;
    private String mainBadgeName;
    private String mainBadgeIcon;
    private int xpEarned;
    private int subBadgesEarned;
    private int subBadgesTotal;
    private String levelName;
    private String levelLabel;
    private String levelColor;
    private String levelIcon;
}
