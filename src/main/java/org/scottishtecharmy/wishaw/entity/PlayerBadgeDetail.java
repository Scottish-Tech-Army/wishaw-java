package org.scottishtecharmy.wishaw.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "player_badge_detail")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerBadgeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_badge_id", nullable = false)
    private SubBadge subBadge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "earned_date")
    private LocalDate earnedDate;

    @Column(name = "approved")
    private boolean approved = false;

    @Column(name = "evidence_url", length = 1000)
    private String evidenceUrl;

    @Lob
    @Column(name = "evidence_data", columnDefinition = "BLOB")
    private byte[] evidenceData;

    @Column(name = "coach_notes", length = 1000)
    private String coachNotes;
}
