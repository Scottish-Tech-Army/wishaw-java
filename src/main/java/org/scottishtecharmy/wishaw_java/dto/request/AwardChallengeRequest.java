package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwardChallengeRequest {
    @NotNull(message = "Player ID is required")
    private Long playerId;

    @NotNull(message = "Challenge ID is required")
    private Long challengeId;

    private String notes;
}
