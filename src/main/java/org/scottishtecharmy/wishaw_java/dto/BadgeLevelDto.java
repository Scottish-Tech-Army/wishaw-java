package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeLevelDto {
    private String name;
    private String label;
    private int minXP;
    private Integer maxXP;
    private String color;
    private String icon;
}
