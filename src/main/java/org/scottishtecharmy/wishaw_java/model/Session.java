package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A weekly session / lesson within a module.
 */
@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    /** Week number within the module (1-based) */
    private int weekNumber;

    private String title;

    /** Markdown/plain-text session plan for the facilitator */
    @Column(length = 5000)
    private String sessionPlan;

    /** Delivery notes — tips, timings, differentiation advice */
    @Column(length = 2000)
    private String deliveryNotes;

    /** Attached lesson resources */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Resource> resources = new ArrayList<>();
}
