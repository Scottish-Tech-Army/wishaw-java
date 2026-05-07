package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * A module as managed by an admin.
 * GET /api/v1/admin/modules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminModuleDto {
    private Long id;
    private String name;
    /** Primary game, e.g. "Fortnite" */
    private String game;
    /** Overall learning outcome / goal */
    private String outcome;
    /** Duration in weeks (12–16) */
    private int durationWeeks;
    /** Status: Active, Draft, Archived */
    private String status;
    /** Sub-badges / challenges within this module */
    private List<AdminSubBadgeDto> subBadges;
    /** Names of groups currently using this module */
    private List<String> groupsUsingIt;
    /** Weekly session plans / lessons */
    private List<AdminSessionDto> sessions;
}
