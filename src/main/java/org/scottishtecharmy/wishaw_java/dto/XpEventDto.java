package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XpEventDto {
    private long id;
    private String activity;
    private int xp;
    private String date;
    private String icon;
}
