package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findByTournamentIdOrderByRankPositionAsc(Long tournamentId);
    List<Ranking> findByPlayerId(Long playerId);
    Optional<Ranking> findByPlayerIdAndTournamentId(Long playerId, Long tournamentId);
}

