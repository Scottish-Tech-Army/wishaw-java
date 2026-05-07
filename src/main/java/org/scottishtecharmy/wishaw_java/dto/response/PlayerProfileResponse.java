package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * // FRONTEND_INTEGRATION: This DTO is used by the React player profile page.
 * // FRONTEND_INTEGRATION: UI will show category totals, current level, and module progress from this response.
 * // FRONTEND_INTEGRATION: Do not rename JSON fields without updating frontend contract.
 */
@Getter
@Builder
@AllArgsConstructor
public class PlayerProfileResponse {
    private Long id;
    private String username;
    private String displayName;
    private String centreName;
    private String groupName;
    private List<BadgeProgressResponse> badgeProgress;
    private int overallTotalPoints;
}
