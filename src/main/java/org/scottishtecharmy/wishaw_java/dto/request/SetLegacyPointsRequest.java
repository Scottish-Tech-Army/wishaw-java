package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetLegacyPointsRequest {
    @NotNull(message = "Player ID is required")
    private Long playerId;

    @NotNull(message = "Badge category ID is required")
    private Long badgeCategoryId;

    @Min(value = 0, message = "Legacy points must be zero or positive")
    private int legacyPoints;
}
