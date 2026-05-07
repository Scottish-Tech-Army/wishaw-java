package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentType;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {

    @Id
    private String id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    private String description;
    private String rulesText;
    private String venue;

    @Enumerated(EnumType.STRING)
    private TournamentType type;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime regStartDate;
    private LocalDateTime regEndDate;
    private int capacity;
    private int participantCount;
    private Integer teamMinSize;
    private Integer teamMaxSize;
    private int pointsWin;
    private int pointsDraw;
    private int pointsLoss;
}
