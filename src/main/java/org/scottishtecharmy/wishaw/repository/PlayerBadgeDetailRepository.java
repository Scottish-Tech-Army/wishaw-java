package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.PlayerBadgeDetail;
import org.scottishtecharmy.wishaw.entity.Player;
import org.scottishtecharmy.wishaw.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerBadgeDetailRepository extends JpaRepository<PlayerBadgeDetail, Long> {
    List<PlayerBadgeDetail> findByPlayer(Player player);
    List<PlayerBadgeDetail> findByPlayerAndBadge(Player player, Badge badge);
    List<PlayerBadgeDetail> findByPlayerAndApproved(Player player, boolean approved);
    boolean existsByPlayerAndSubBadgeId(Player player, Long subBadgeId);
}
