package org.scottishtecharmy.wishaw_java.group;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;
import org.scottishtecharmy.wishaw_java.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "game_group")
public class GameGroup extends BaseEntity {

    private String name;

    @ManyToOne
    @JoinColumn(name = "centre_id")
    private Centre centre;

    @ManyToMany
    @JoinTable(
            name = "group_member",
            joinColumns = @JoinColumn(name = "game_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();
}
