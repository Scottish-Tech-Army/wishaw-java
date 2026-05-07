package org.scottishtecharmy.wishaw_java.dto;

import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchDTO {
    private Long id;

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    private String tournamentName;
    private String matchTitle;
    private Integer roundNumber;
    private Long teamAId;
    private String teamAName;
    private Long teamBId;
    private String teamBName;
    private Long playerAId;
    private String playerAName;
    private Long playerBId;
    private String playerBName;
    private MatchStatus status;
    private String scheduledTime;
    private String venue;
    private Long winnerTeamId;
    private String winnerTeamName;
    private Long winnerPlayerId;
    private String winnerPlayerName;
    private String createdAt;
    private String updatedAt;
}

