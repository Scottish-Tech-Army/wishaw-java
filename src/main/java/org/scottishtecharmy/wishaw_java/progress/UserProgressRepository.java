package org.scottishtecharmy.wishaw_java.progress;

import org.scottishtecharmy.wishaw_java.badge.Badge;
import org.scottishtecharmy.wishaw_java.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserAndBadge(User user, Badge badge);
    List<UserProgress> findByUser(User user);
    List<UserProgress> findByUserId(Long userId);

    @Query("SELECT up.user.id, SUM(up.totalPoints) FROM UserProgress up GROUP BY up.user.id ORDER BY SUM(up.totalPoints) DESC")
    List<Object[]> findGlobalLeaderboard();

    @Query("SELECT up.user.id, SUM(up.totalPoints) FROM UserProgress up WHERE up.user.centre.id = :centreId GROUP BY up.user.id ORDER BY SUM(up.totalPoints) DESC")
    List<Object[]> findLeaderboardByCentre(Long centreId);
}
