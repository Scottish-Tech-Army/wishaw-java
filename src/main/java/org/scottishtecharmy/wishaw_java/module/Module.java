package org.scottishtecharmy.wishaw_java.module;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;

@Entity
@Getter
@Setter
public class Module extends BaseEntity {

    private String name;
    private String description;
    private boolean approved;

    @ManyToOne
    @JoinColumn(name = "centre_id")
    private Centre centre;
}
