package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * Request body for updating a session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSessionRequestDto {
    private Integer weekNumber;
    private String title;
    private String sessionPlan;
    private String deliveryNotes;
}
