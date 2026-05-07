package org.scottishtecharmy.wishaw_java.legacy.dto;

public record LegacyPointsResponse(
        Long id,
        Long userId,
        String username,
        String displayName,
        Long badgeId,
        String badgeName,
        int points,
        String reason
) { }

