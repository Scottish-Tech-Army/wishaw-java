package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badge_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Lowercase stable key, e.g. "bronze" */
    @Column(nullable = false)
    private String name;

    /** Human-readable label, e.g. "Bronze" */
    private String label;

    /** Minimum XP required (inclusive) */
    private int minXP;

    /** Maximum XP for this level (null for top level) */
    private Integer maxXP;

    /** CSS-compatible hex colour string */
    private String color;

    /** Emoji icon */
    private String icon;

    /** Sort order (lower = earlier level) */
    private int sortOrder;
}
