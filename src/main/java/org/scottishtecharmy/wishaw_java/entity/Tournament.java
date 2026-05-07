package org.scottishtecharmy.wishaw_java.entity;

import com.ltc.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.scottishtecharmy.wishaw_java.enums.OrganizationType;
import org.scottishtecharmy.wishaw_java.enums.ParticipationType;
import org.scottishtecharmy.wishaw_java.enums.SportType;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationType participationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationType organizationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    private Integer maxParticipants;

    private LocalDateTime registrationStartDate;

    private LocalDateTime registrationEndDate;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    private String rules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = TournamentStatus.UPCOMING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

