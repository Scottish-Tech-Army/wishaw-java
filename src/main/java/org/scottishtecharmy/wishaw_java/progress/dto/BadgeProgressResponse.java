package org.scottishtecharmy.wishaw_java.progress.dto;

import java.util.List;

public record BadgeProgressResponse(
        Long badgeId,
        String badgeName,
        int totalPoints,
        String level,
        List<EarnedSubBadge> earnedSubBadges
) {
    public record EarnedSubBadge(
            Long id,
            String name,
            int points
    ) { }
}
