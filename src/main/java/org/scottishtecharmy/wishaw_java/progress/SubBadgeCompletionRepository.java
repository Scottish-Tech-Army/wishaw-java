package org.scottishtecharmy.wishaw_java.progress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubBadgeCompletionRepository extends JpaRepository<SubBadgeCompletion, Long> {
    List<SubBadgeCompletion> findByUserId(Long userId);
    List<SubBadgeCompletion> findByUserIdAndSubBadgeBadgeId(Long userId, Long badgeId);
    Optional<SubBadgeCompletion> findByUserIdAndSubBadgeId(Long userId, Long subBadgeId);
    boolean existsByUserIdAndSubBadgeId(Long userId, Long subBadgeId);
    long countByUserId(Long userId);
}
