package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalorieRecordDTO {
    private Long id;

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private String playerName;
    private Long matchId;
    private String matchTitle;
    private Long tournamentId;
    private String tournamentName;

    @NotNull(message = "Calories burned is required")
    private Double caloriesBurned;

    private Long enteredById;
    private String enteredByName;
    private String createdAt;
}

