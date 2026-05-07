package org.scottishtecharmy.wishaw_java.badge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubBadgeRepository extends JpaRepository<SubBadge, Long> {
    List<SubBadge> findByModuleId(Long moduleId);
    List<SubBadge> findByBadgeId(Long badgeId);
}

