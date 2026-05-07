package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeCatalogueDto {
    private List<BadgeLevelDto> badgeLevels;
    private List<MainBadgeDetailDto> badges;
}
