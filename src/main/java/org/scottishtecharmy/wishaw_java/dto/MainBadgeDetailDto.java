package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainBadgeDetailDto {
    private String id;
    private String icon;
    private String name;
    private String tagline;
    private String description;
    private int xpEarned;
    private List<SubBadgeDetailDto> subBadges;
}
