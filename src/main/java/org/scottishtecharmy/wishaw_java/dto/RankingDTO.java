package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RankingDTO {
    private Long id;

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private String playerName;

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    private String tournamentName;

    @NotNull(message = "Rank position is required")
    private Integer rankPosition;

    private Integer totalPoints;
    private Integer wins;
    private Integer losses;
    private Integer draws;
    private String createdAt;
    private String updatedAt;
}

