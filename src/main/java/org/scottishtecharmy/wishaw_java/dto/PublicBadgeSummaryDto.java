package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicBadgeSummaryDto {
    private List<MainBadgeSummaryDto> badges;
}
