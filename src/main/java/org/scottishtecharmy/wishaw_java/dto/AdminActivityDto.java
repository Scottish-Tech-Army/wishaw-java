package org.scottishtecharmy.wishaw_java.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recent activity item for admin dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityDto {
    private Long id;
    private String type;     // "badge", "module", "user", "xp"
    private String icon;
    private String action;
    private String centre;
    private String admin;    // who performed the action
    private String time;     // relative time string, e.g. "5 minutes ago"
}
