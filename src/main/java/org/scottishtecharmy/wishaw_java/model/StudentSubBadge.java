package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_sub_badges",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "sub_badge_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_badge_id", nullable = false)
    private SubBadge subBadge;

    private boolean earned;

    /** Display date string, e.g. "Nov 2024" */
    private String earnedDate;
}
