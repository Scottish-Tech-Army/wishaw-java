package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardXpRequestDto {
    private int xp;
    private String reason;
}
