package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * Request body for creating a new module.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateModuleRequestDto {
    private String name;
    private String game;
    private String outcome;
    private int durationWeeks;
    private String status;
}
