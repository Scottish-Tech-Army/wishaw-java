package org.scottishtecharmy.wishaw_java.centre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;

@Entity
@Getter
@Setter
public class Centre extends BaseEntity {

    private String name;

    @Column(unique = true)
    private String code; // e.g. WISHAW
}
