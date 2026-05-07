package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleSubBadgeDto {
    private long id;
    private String icon;
    private String name;
    private String desc;
    private int xpReward;
    private String mainBadgeId;
    private boolean earned;
    private String earnedDate;
}
