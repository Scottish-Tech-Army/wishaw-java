package org.scottishtecharmy.wishaw_java.badge.dto;

import java.time.LocalDateTime;

public record SubBadgeResponse(
        Long id,
        String name,
        int points,
        Long badgeId,
        String badgeName,
        Long moduleId,
        String moduleName,
        LocalDateTime createdAt
) { }

