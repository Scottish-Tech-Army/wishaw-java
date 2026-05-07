package org.scottishtecharmy.wishaw_java.progress;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.badge.Badge;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;
import org.scottishtecharmy.wishaw_java.user.User;

@Entity
@Getter
@Setter
public class UserProgress extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    private int totalPoints;
}
