package org.scottishtecharmy.wishaw_java.badge.dto;

import java.time.LocalDateTime;

public record BadgeResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) { }

