package org.scottishtecharmy.wishaw_java.legacy.dto;

public record LegacyPointsRequest(
        Long userId,
        Long badgeId,
        int points,
        String reason
) { }

