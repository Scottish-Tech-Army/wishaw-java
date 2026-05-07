package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "main_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stable slug, e.g. "game-mastery" */
    @Column(unique = true, nullable = false)
    private String slug;

    private String icon;

    @Column(nullable = false)
    private String name;

    private String tagline;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "mainBadge", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubBadge> subBadges = new ArrayList<>();
}
