package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String icon;

    @Column(nullable = false)
    private String name;

    /** Primary game, e.g. "Fortnite" */
    private String game;

    @Column(length = 1000)
    private String outcome;

    private int durationWeeks;

    /** Status: Active, Draft, Archived */
    @Column(nullable = false)
    @Builder.Default
    private String status = "Draft";

    /** SubBadges that belong to this module */
    @ManyToMany
    @JoinTable(
        name = "module_sub_badges",
        joinColumns = @JoinColumn(name = "module_id"),
        inverseJoinColumns = @JoinColumn(name = "sub_badge_id")
    )
    @Builder.Default
    private List<SubBadge> subBadges = new ArrayList<>();

    /** Sessions/lessons within this module */
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("weekNumber ASC")
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();
}
