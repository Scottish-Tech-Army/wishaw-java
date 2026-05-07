package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.PlayerModuleEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerModuleEnrollmentRepository extends JpaRepository<PlayerModuleEnrollment, Long> {
    List<PlayerModuleEnrollment> findByPlayerId(Long playerId);
    List<PlayerModuleEnrollment> findByModuleId(Long moduleId);
    void deleteByPlayerId(Long playerId);
    void deleteByModuleId(Long moduleId);
}
