package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "main_badges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainBadge {

    @Id
    private String id;
    private String name;
    private String description;
    private String icon;
}
