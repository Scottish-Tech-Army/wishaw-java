package org.scottishtecharmy.wishaw_java.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModuleRequest(
        @NotBlank(message = "name is required")
        String name,

        String description,

        @NotNull(message = "centreId is required")
        Long centreId
) { }

