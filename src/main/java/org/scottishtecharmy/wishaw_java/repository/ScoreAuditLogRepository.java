package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ScoreAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreAuditLogRepository extends JpaRepository<ScoreAuditLog, Long> {
    List<ScoreAuditLog> findByScoreId(Long scoreId);
    List<ScoreAuditLog> findByMatchId(Long matchId);
    List<ScoreAuditLog> findByEditedById(Long editorId);
}

