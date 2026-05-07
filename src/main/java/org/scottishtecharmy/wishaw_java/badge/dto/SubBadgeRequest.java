package org.scottishtecharmy.wishaw_java.badge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubBadgeRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "points is required")
        @Min(value = 0, message = "points must be >= 0")
        Integer points,

        @NotNull(message = "badgeId is required")
        Long badgeId,

        @NotNull(message = "moduleId is required")
        Long moduleId
) { }

