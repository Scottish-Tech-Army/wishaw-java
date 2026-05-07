package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xp_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XpEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String activity;

    private int xp;

    /** ISO-8601 date string, e.g. "2025-03-21" */
    private String date;

    private String icon;
}
