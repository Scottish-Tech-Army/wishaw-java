package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.CalorieRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CalorieRecordRepository extends JpaRepository<CalorieRecord, Long> {
    List<CalorieRecord> findByPlayerId(Long playerId);
    List<CalorieRecord> findByMatchId(Long matchId);
    List<CalorieRecord> findByTournamentId(Long tournamentId);
    List<CalorieRecord> findByPlayerIdAndTournamentId(Long playerId, Long tournamentId);

    @Query("SELECT SUM(c.caloriesBurned) FROM CalorieRecord c WHERE c.player.id = :playerId")
    Double sumCaloriesByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT SUM(c.caloriesBurned) FROM CalorieRecord c WHERE c.player.id = :playerId AND c.tournament.id = :tournamentId")
    Double sumCaloriesByPlayerIdAndTournamentId(@Param("playerId") Long playerId, @Param("tournamentId") Long tournamentId);
}

