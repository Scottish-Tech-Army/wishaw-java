package org.scottishtecharmy.wishaw_java.progress.dto;

import java.time.LocalDate;
import java.util.List;

public record UserProfileResponse(
        Long userId,
        String username,
        String displayName,
        String centreName,
        String profileImageUrl,
        LocalDate dob,
        List<BadgeProgressResponse> badges,
        int overallXp,
        long completedSubBadges
) { }
