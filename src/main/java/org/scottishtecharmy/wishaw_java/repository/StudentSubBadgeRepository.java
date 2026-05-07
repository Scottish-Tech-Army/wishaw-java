package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.StudentSubBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubBadgeRepository extends JpaRepository<StudentSubBadge, Long> {
    List<StudentSubBadge> findByStudentId(Long studentId);
    Optional<StudentSubBadge> findByStudentIdAndSubBadgeId(Long studentId, Long subBadgeId);
    long countByStudentIdAndEarnedTrue(Long studentId);
    List<StudentSubBadge> findByStudentIdAndSubBadgeMainBadgeId(Long studentId, Long mainBadgeId);
}
