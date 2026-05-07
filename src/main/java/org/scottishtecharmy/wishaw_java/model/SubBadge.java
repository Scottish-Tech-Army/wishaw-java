package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sub_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String icon;

    @Column(nullable = false)
    private String name;

    private String shortDesc;

    @Column(length = 1000)
    private String criteria;

    private int xpReward;

    /** "lesson" or "activity" */
    private String type;

    /** Comma-separated skill tags */
    @Column(length = 1000)
    private String skills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_badge_id", nullable = false)
    private MainBadge mainBadge;
}
