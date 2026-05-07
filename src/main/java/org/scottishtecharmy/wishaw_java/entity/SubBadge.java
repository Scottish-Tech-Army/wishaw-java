package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sub_badges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubBadge {

    @Id
    private String id;
    private String name;
    private String description;
    private int points;

    @Lob
    private String skillsCsv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_badge_id")
    private MainBadge mainBadge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private LearningModule learningModule;
}
