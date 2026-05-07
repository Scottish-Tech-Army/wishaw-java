package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamSummaryDto {
    private String id;
    private String name;
    private String icon;
    private String colour;
    private String hub;
    private String founded;
    private String description;
    private String game;
    private int memberCount;
    private String captainGamertag;
    private List<String> memberAvatarUrls;
}
