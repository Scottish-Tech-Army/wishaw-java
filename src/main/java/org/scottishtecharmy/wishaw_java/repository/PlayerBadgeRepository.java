package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.PlayerBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerBadgeRepository extends JpaRepository<PlayerBadge, Long> {
    List<PlayerBadge> findByPlayerId(Long playerId);
    List<PlayerBadge> findByTournamentId(Long tournamentId);
    List<PlayerBadge> findByPlayerIdAndTournamentId(Long playerId, Long tournamentId);
    boolean existsByPlayerIdAndBadgeIdAndTournamentId(Long playerId, Long badgeId, Long tournamentId);
}

