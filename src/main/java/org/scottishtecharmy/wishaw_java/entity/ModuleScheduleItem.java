package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "module_schedule_items")
@Getter
@Setter
@NoArgsConstructor
public class ModuleScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    private int weekNumber;

    private String sessionFocus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_challenge_id")
    private Challenge linkedChallenge;

    private String sessionPlanUrl;
    private String sessionSlidesUrl;

    private int displayOrder;
}
