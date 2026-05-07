package org.scottishtecharmy.wishaw_java.legacy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LegacyPointsRepository extends JpaRepository<LegacyPoints, Long> {
    List<LegacyPoints> findByUserId(Long userId);
    List<LegacyPoints> findByBadgeId(Long badgeId);
    Optional<LegacyPoints> findByUserIdAndBadgeId(Long userId, Long badgeId);
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    @Query("SELECT lp.user.id, SUM(lp.points) FROM LegacyPoints lp GROUP BY lp.user.id")
    List<Object[]> findLegacyTotalsByUser();

    @Query("SELECT lp.user.id, SUM(lp.points) FROM LegacyPoints lp WHERE lp.user.centre.id = :centreId GROUP BY lp.user.id")
    List<Object[]> findLegacyTotalsByUserAndCentre(Long centreId);
}

