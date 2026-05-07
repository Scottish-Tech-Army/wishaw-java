package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreAuditLogDTO {
    private Long id;
    private Long scoreId;
    private Long matchId;
    private Integer previousScore;
    private Integer newScore;
    private String previousDetails;
    private String newDetails;
    private Long editedById;
    private String editedByName;
    private String editedAt;
}

