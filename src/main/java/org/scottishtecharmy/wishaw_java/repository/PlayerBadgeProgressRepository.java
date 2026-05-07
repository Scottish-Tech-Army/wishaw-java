package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerBadgeProgressRepository extends JpaRepository<PlayerBadgeProgress, Long> {
    List<PlayerBadgeProgress> findByPlayerId(Long playerId);
    Optional<PlayerBadgeProgress> findByPlayerIdAndBadgeCategoryId(Long playerId, Long badgeCategoryId);

    @Query("SELECT p FROM PlayerBadgeProgress p WHERE p.player.id = :playerId AND p.badgeCategory.code = :categoryCode")
    Optional<PlayerBadgeProgress> findByPlayerIdAndCategoryCode(@Param("playerId") Long playerId, @Param("categoryCode") String categoryCode);

    void deleteByPlayerId(Long playerId);
}
