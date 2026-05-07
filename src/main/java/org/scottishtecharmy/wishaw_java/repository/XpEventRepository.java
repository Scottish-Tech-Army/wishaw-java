package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.XpEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XpEventRepository extends JpaRepository<XpEvent, Long> {
    List<XpEvent> findByStudentIdOrderByDateDesc(Long studentId);
    List<XpEvent> findTop5ByStudentIdOrderByDateDesc(Long studentId);
    List<XpEvent> findByStudentIdAndDateGreaterThanEqual(Long studentId, String date);
    
    /** Get most recent XP events across all students for admin dashboard */
    List<XpEvent> findTop20ByOrderByDateDescIdDesc();
    
    /** Count XP events since a given date (for "badges awarded this week") */
    long countByDateGreaterThanEqual(String date);
}
