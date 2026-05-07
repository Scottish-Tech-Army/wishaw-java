package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.MainBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MainBadgeRepository extends JpaRepository<MainBadge, Long> {
    Optional<MainBadge> findBySlug(String slug);
}
