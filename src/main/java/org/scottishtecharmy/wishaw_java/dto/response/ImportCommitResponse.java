package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ImportCommitResponse {
    private Long batchId;
    private String status;
    private int totalAwardsCreated;
    private int totalPlayersAffected;
    private String message;
}
