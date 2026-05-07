package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreDTO {
    private Long id;

    @NotNull(message = "Match ID is required")
    private Long matchId;

    private Long playerId;
    private String playerName;
    private Long teamId;
    private String teamName;

    @NotNull(message = "Score value is required")
    private Integer scoreValue;

    private String scoreDetails;
    private Long updatedById;
    private String updatedByName;
    private String createdAt;
    private String updatedAt;
}

