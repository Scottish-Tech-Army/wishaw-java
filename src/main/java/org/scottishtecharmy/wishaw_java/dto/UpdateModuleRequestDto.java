package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * Request body for updating a module.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateModuleRequestDto {
    private String name;
    private String game;
    private String outcome;
    private Integer durationWeeks;
    private String status;
}
