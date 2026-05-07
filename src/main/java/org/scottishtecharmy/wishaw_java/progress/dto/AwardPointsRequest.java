package org.scottishtecharmy.wishaw_java.progress.dto;

public record AwardPointsRequest(
        Long userId,
        Long subBadgeId
) { }

