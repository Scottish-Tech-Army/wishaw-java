package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.BadgeLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeLevelRepository extends JpaRepository<BadgeLevel, Long> {
    List<BadgeLevel> findAllByOrderBySortOrderAsc();
}
