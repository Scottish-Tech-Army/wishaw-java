package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private int rank;
    private Long playerId;
    private String displayName;
    private String centreName;
    private String groupName;
    private int totalPoints;
    private String highestLevel;
}
