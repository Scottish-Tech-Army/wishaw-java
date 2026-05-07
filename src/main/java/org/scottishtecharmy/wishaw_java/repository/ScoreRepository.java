package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByMatchId(Long matchId);
    List<Score> findByPlayerId(Long playerId);
    List<Score> findByTeamId(Long teamId);
    Optional<Score> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    Optional<Score> findByMatchIdAndTeamId(Long matchId, Long teamId);
}

