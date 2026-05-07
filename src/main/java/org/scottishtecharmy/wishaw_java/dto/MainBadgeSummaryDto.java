package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainBadgeSummaryDto {
    private String id;
    private String icon;
    private String name;
    private int xpEarned;
    private String levelName;
    private String levelLabel;
    private String levelColor;
    private String levelIcon;
    private int subBadgesEarned;
    private int subBadgesTotal;
}
