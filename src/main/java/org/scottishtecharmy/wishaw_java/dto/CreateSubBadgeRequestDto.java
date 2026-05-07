package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * Request body for creating a new sub-badge.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubBadgeRequestDto {
    private String name;
    private String description;
    private String mainBadgeId;
    private int xpValue;
    private List<String> skills;
}
