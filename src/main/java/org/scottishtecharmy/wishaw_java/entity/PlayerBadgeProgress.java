package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_badge_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "badge_category_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PlayerBadgeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private UserAccount player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_category_id", nullable = false)
    private BadgeCategory badgeCategory;

    private int legacyPoints = 0;
    private int earnedPoints = 0;
    private int totalPoints = 0; // totalPoints = legacyPoints + earnedPoints

    private String currentLevelName;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        totalPoints = legacyPoints + earnedPoints;
        updatedAt = LocalDateTime.now();
    }
}
