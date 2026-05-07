package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * Request body for creating a new session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSessionRequestDto {
    private int weekNumber;
    private String title;
    private String sessionPlan;
    private String deliveryNotes;
}
