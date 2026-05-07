package org.scottishtecharmy.wishaw_java.module.dto;

import java.time.LocalDateTime;

public record ModuleResponse(
        Long id,
        String name,
        String description,
        boolean approved,
        Long centreId,
        String centreName,
        LocalDateTime createdAt
) { }

