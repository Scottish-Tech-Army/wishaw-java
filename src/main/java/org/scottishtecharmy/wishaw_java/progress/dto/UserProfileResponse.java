package org.scottishtecharmy.wishaw_java.progress.dto;

import java.util.List;

public record UserProfileResponse(
        Long userId,
        String username,
        String displayName,
        String centreName,
        String profileImageUrl,
        List<BadgeProgressResponse> badges,
        int overallXp,
        long completedSubBadges
) { }
