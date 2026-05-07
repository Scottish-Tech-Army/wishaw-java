package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

/**
 * A weekly session / lesson within a module.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSessionDto {
    private Long id;
    /** Week number within the module (1-based) */
    private int weekNumber;
    private String title;
    /** Markdown/plain-text session plan for the facilitator */
    private String sessionPlan;
    /** Delivery notes — tips, timings, differentiation advice */
    private String deliveryNotes;
    /** Attached lesson resources (slides, videos, handouts) */
    private List<AdminResourceDto> resources;
}
