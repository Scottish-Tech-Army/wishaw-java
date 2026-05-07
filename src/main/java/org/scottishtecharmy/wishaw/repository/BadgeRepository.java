package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.Badge;
import org.scottishtecharmy.wishaw.entity.Centre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByCentre(Centre centre);
    List<Badge> findByCentreIsNull();
}
