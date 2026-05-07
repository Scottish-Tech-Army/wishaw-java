package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sport {

    @Id
    private String id;
    private String name;
    private String icon;
    private String description;

    @Lob
    private String scoreFieldsJson;

    private int rankingWin;
    private int rankingDraw;
    private int rankingLoss;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;
}
