package org.scottishtecharmy.wishaw_java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicPlayerProfileDto {
    private String username;
    private String gamertag;
    private String realName;
    private String bio;
    private String joinedDate;
    private int level;
    private int totalXP;
    private String avatarUrl;
    private String teamName;
    private String teamIcon;
    private String teamId;
    private String teamColour;
    private String hub;

    @JsonProperty("isCaptain")
    private boolean isCaptain;
    private Integer globalRank;
    private List<PublicModuleProgressDto> moduleProgress;
}
