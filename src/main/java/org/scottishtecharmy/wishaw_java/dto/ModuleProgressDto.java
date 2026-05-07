package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleProgressDto {
    private long id;
    private String icon;
    private String name;
    private String outcome;
    private int durationWeeks;
    private List<ModuleSubBadgeDto> subBadges;
}
