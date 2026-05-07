package org.scottishtecharmy.wishaw_java.badge;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;

@Entity
@Getter
@Setter
public class Badge extends BaseEntity {

    private String name; // Game Mastery, Teamwork etc.
    private String description;
}
