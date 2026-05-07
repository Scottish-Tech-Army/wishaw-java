package org.scottishtecharmy.wishaw_java.level.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LevelRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "minPoints is required")
        @Min(value = 0, message = "minPoints must be >= 0")
        Integer minPoints,

        @NotNull(message = "maxPoints is required")
        Integer maxPoints,

        Integer displayOrder
) { }

