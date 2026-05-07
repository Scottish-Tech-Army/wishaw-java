package org.scottishtecharmy.wishaw_java.badge;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;
import org.scottishtecharmy.wishaw_java.module.Module;

@Entity
@Getter
@Setter
public class SubBadge extends BaseEntity {

    private String name;
    private int points;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
