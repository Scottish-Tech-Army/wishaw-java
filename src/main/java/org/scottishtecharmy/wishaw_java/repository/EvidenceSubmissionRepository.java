package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.EvidenceSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceSubmissionRepository extends JpaRepository<EvidenceSubmission, Long> {
    List<EvidenceSubmission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    List<EvidenceSubmission> findByStatusOrderBySubmittedAtDesc(String status);
}
