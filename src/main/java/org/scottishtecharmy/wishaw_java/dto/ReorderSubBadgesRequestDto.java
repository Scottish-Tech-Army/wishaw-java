package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * Request body for reordering sub-badges.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReorderSubBadgesRequestDto {
    private List<Long> orderedIds;
}
