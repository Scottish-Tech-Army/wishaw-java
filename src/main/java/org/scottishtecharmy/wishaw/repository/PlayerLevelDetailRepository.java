package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.PlayerLevelDetail;
import org.scottishtecharmy.wishaw.entity.Player;
import org.scottishtecharmy.wishaw.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerLevelDetailRepository extends JpaRepository<PlayerLevelDetail, Long> {
    List<PlayerLevelDetail> findByPlayer(Player player);
    Optional<PlayerLevelDetail> findByPlayerAndBadge(Player player, Badge badge);
}
