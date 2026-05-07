package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerBadgeDTO {
    private Long id;

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private String playerName;

    @NotNull(message = "Badge ID is required")
    private Long badgeId;

    private String badgeName;
    private String badgeDescription;
    private String badgeIconUrl;
    private Long tournamentId;
    private String tournamentName;
    private Long awardedById;
    private String awardedByName;
    private String awardedAt;
}

