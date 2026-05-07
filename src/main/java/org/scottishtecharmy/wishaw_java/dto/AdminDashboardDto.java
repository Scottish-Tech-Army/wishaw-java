package org.scottishtecharmy.wishaw_java.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dashboard summary data for admin portal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private int totalUsers;
    private int activeGroups;
    private int modulesInProgress;
    private int badgesAwardedThisWeek;
    private int centreCount;
    private List<AdminActivityDto> recentActivity;
}
