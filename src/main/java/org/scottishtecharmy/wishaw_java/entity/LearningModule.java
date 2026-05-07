package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.enums.ModuleStatus;

@Entity
@Table(name = "learning_modules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningModule {

    @Id
    private String id;
    private String name;
    private String game;
    private String description;
    private int durationWeeks;

    @Enumerated(EnumType.STRING)
    private ModuleStatus status;
}
