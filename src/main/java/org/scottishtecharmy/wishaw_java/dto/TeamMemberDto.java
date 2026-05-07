package org.scottishtecharmy.wishaw_java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDto {
    private long studentId;
    private String gamertag;
    private String realName;
    private String username;
    private String teamId;
    private String joinedDate;
    private String avatarUrl;

    @JsonProperty("isCaptain")
    private boolean isCaptain;
    private int level;
    private int totalXP;
    private List<TeamMemberBadgeProgressDto> badgeProgress;
    private List<TeamMemberModuleProgressDto> moduleProgress;
}
