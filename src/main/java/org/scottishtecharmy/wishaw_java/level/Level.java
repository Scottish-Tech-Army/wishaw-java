package org.scottishtecharmy.wishaw_java.level;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;

@Entity
@Getter
@Setter
public class Level extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;           // e.g. BRONZE, SILVER, GOLD, PLATINUM

    @Column(nullable = false)
    private int minPoints;         // inclusive lower bound

    @Column(nullable = false)
    private int maxPoints;         // inclusive upper bound  (-1 = unlimited)

    private int displayOrder;      // for sorting in UI
}
