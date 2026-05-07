package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEnrollmentRequest {
    @NotNull(message = "Player ID is required")
    private Long playerId;

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    private Long groupId;
}
