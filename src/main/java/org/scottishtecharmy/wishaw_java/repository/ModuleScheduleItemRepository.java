package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ModuleScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleScheduleItemRepository extends JpaRepository<ModuleScheduleItem, Long> {
    List<ModuleScheduleItem> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);
}
