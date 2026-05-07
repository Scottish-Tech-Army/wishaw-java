package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parent_links")
@Getter
@Setter
@NoArgsConstructor
public class ParentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private UserAccount parentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_user_id", nullable = false)
    private UserAccount playerUser;

    private String relationshipLabel;
}
