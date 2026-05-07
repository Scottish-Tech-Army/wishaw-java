package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Attendance;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByMatchId(Long matchId);
    List<Attendance> findByPlayerId(Long playerId);
    Optional<Attendance> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    long countByPlayerIdAndStatus(Long playerId, AttendanceStatus status);
    boolean existsByMatchIdAndPlayerId(Long matchId, Long playerId);
}

