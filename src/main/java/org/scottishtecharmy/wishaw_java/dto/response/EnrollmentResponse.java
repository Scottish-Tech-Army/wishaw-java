package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long playerId;
    private String playerName;
    private Long moduleId;
    private String moduleName;
    private String status;
}
