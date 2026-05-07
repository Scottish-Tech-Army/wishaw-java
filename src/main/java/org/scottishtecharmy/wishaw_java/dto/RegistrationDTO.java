package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistrationDTO {
    private Long id;

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    private String tournamentName;

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private String playerName;
    private Long teamId;
    private String teamName;
    private String registeredAt;
}

