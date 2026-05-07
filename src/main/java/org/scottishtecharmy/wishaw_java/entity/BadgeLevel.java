package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "badge_levels")
@Getter
@Setter
@NoArgsConstructor
public class BadgeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int minPoints;

    private Integer maxPoints; // nullable for open-ended upper bound

    @Column(nullable = false)
    private int rankOrder;

    private boolean active = true;
}
