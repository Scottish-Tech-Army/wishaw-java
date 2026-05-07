package org.scottishtecharmy.wishaw_java.badge.dto;

import jakarta.validation.constraints.NotBlank;

public record BadgeRequest(
        @NotBlank(message = "name is required")
        String name,

        String description
) { }

