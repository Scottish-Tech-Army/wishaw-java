package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubBadgeDetailDto {
    private long id;
    private String icon;
    private String name;
    private String shortDesc;
    private String criteria;
    private int xpReward;
    private String type;
    private List<String> skills;
    private boolean earned;
    private String earnedDate;
}
