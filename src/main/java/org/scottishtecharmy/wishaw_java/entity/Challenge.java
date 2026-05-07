package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_category_id", nullable = false)
    private BadgeCategory badgeCategory;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int points;

    private int displayOrder;

    private boolean active = true;
}
