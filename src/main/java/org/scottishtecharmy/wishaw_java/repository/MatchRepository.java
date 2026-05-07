package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Match;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentId(Long tournamentId);
    List<Match> findByTournamentIdAndStatus(Long tournamentId, MatchStatus status);
    List<Match> findByPlayerAIdOrPlayerBId(Long playerAId, Long playerBId);
    List<Match> findByTeamAIdOrTeamBId(Long teamAId, Long teamBId);
    List<Match> findByTournamentIdAndRoundNumber(Long tournamentId, Integer roundNumber);
}

