package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.enums.EnrollmentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_module_enrollments")
@Getter
@Setter
@NoArgsConstructor
public class PlayerModuleEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private UserAccount player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ASSIGNED;

    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
}
