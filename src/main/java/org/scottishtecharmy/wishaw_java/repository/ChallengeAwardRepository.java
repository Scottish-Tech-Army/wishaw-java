package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ChallengeAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeAwardRepository extends JpaRepository<ChallengeAward, Long> {
    List<ChallengeAward> findByPlayerId(Long playerId);
    List<ChallengeAward> findByPlayerIdAndBadgeCategoryId(Long playerId, Long badgeCategoryId);

    @Query("SELECT COALESCE(SUM(ca.awardedPoints), 0) FROM ChallengeAward ca WHERE ca.player.id = :playerId AND ca.badgeCategory.id = :categoryId")
    int sumPointsByPlayerAndCategory(@Param("playerId") Long playerId, @Param("categoryId") Long categoryId);

    boolean existsByPlayerIdAndChallengeIdAndSourceReference(Long playerId, Long challengeId, String sourceReference);
    List<ChallengeAward> findByImportBatchId(Long importBatchId);

    void deleteByPlayerId(Long playerId);

    @Modifying
    @Query("UPDATE ChallengeAward ca SET ca.awardedBy = null WHERE ca.awardedBy.id = :userId")
    void nullifyAwardedBy(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChallengeAward ca SET ca.challenge = null WHERE ca.challenge.id = :challengeId")
    void nullifyChallengeId(@Param("challengeId") Long challengeId);

    @Modifying
    @Query("UPDATE ChallengeAward ca SET ca.module = null WHERE ca.module.id = :moduleId")
    void nullifyModuleId(@Param("moduleId") Long moduleId);
}
