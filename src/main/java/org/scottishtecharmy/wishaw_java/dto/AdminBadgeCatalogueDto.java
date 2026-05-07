package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Admin-facing badge catalogue with per-badge leaderboard data.
 * GET /api/v1/admin/badges
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBadgeCatalogueDto {
    private List<BadgeLevelDto> badgeLevels;
    private List<MainBadgeDetailDto> badges;
    /** Top earners per badge, keyed by badge id (slug) */
    private Map<String, List<BadgeLeaderboardEntryDto>> badgeLeaderboards;
}
