package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardPlayerDto {
    private int rank;
    private long studentId;
    private String name;
    private String username;
    private String gamertag;
    private int level;
    private int periodXp;
    private int completedModules;
    private int badgesCompleted;
    private String centre;
    private String avatarUrl;
    private List<String> badgeIcons;
}
