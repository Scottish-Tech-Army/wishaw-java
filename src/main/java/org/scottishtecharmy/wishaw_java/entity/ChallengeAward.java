package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.enums.SourceType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_awards")
@Getter
@Setter
@NoArgsConstructor
public class ChallengeAward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private UserAccount player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_category_id", nullable = false)
    private BadgeCategory badgeCategory;

    @Column(nullable = false)
    private int awardedPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awarded_by")
    private UserAccount awardedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType;

    private String sourceReference;
    private LocalDate awardDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_batch_id")
    private ImportBatch importBatch;

    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (awardDate == null) {
            awardDate = LocalDate.now();
        }
    }
}
