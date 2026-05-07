package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.CaloriesLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaloriesLogRepository extends JpaRepository<CaloriesLog, Long> {
    List<CaloriesLog> findByUserAccount_IdOrderByLoggedAtDesc(String userId);
}
