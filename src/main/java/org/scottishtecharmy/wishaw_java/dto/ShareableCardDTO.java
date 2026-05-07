package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShareableCardDTO {
    private Long playerId;
    private String playerName;
    private String profilePhotoUrl;
    private String achievement;
    private String description;
    private String tournamentName;
    private String sportType;
    private String shareUrl;
    private String whatsappShareUrl;
}

