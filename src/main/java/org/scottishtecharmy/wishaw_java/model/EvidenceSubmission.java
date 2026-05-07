package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evidence_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_badge_id", nullable = false)
    private SubBadge subBadge;

    private String fileName;

    @Column(length = 2000)
    private String notes;

    /** Display string, e.g. "22 Mar 2026" */
    private String submittedAt;

    /** "pending", "approved", or "rejected" */
    @Column(nullable = false)
    private String status;
}
