package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.SubBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubBadgeRepository extends JpaRepository<SubBadge, Long> {
    List<SubBadge> findByMainBadgeId(Long mainBadgeId);
}
