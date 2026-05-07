package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponseDto {
    private String period;
    private List<LeaderboardPlayerDto> players;
    private List<LeaderboardCentreDto> centres;
    private int totalCount;
    private String currentUserUsername;
    private String currentUserCentreName;
}
