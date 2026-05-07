package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameGroup {

    @Id
    private String id;

    private String name;
    private String game;
    private int memberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_id")
    private Centre centre;
}
