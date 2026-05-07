package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * A sub-badge/challenge within a module, as managed by an admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubBadgeDto {
    private Long id;
    private String name;
    private String description;
    /** Stable slug of the main badge this sub-badge contributes to */
    private String mainBadgeId;
    /** XP awarded on completion */
    private int xpValue;
    /** YSOF skill tags */
    private List<String> skills;
}
