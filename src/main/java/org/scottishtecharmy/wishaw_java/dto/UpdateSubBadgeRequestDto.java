package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * Request body for updating a sub-badge.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubBadgeRequestDto {
    private String name;
    private String description;
    private String mainBadgeId;
    private Integer xpValue;
    private List<String> skills;
}
