package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ModuleSessionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleSessionItemRepository extends JpaRepository<ModuleSessionItem, Long> {
    List<ModuleSessionItem> findByLearningModule_IdOrderByWeekNoAsc(String moduleId);
    void deleteByLearningModule_Id(String moduleId);
}
