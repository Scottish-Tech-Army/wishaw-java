package org.scottishtecharmy.wishaw_java.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameGroupRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "centreId is required")
        Long centreId
) { }
