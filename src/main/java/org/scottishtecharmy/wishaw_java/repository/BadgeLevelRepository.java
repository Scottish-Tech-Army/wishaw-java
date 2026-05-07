package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.BadgeLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeLevelRepository extends JpaRepository<BadgeLevel, Long> {
    List<BadgeLevel> findAllByActiveTrueOrderByRankOrderAsc();
}
