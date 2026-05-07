package org.scottishtecharmy.wishaw_java.legacy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.badge.Badge;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;
import org.scottishtecharmy.wishaw_java.user.User;

@Entity
@Getter
@Setter
@Table(name = "legacy_points", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_id"}))
public class LegacyPoints extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    private int points;

    private String reason; // optional description e.g. "Pre-system achievement points"
}

