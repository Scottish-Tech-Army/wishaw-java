package org.scottishtecharmy.wishaw_java.progress;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.badge.SubBadge;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;
import org.scottishtecharmy.wishaw_java.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sub_badge_completion")
public class SubBadgeCompletion extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "sub_badge_id")
    private SubBadge subBadge;

    private LocalDateTime completedAt = LocalDateTime.now();
}

