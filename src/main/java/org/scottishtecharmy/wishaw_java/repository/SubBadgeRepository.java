package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.SubBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubBadgeRepository extends JpaRepository<SubBadge, String> {
    List<SubBadge> findByLearningModule_IdOrderByNameAsc(String moduleId);
    List<SubBadge> findByMainBadge_IdOrderByNameAsc(String mainBadgeId);
    void deleteByLearningModule_Id(String moduleId);
}
