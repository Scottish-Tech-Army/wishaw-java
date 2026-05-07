package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeCategoryRepository extends JpaRepository<BadgeCategory, Long> {
    Optional<BadgeCategory> findByCode(String code);
    boolean existsByCode(String code);
}
