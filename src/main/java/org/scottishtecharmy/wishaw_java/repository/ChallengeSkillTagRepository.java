package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ChallengeSkillTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeSkillTagRepository extends JpaRepository<ChallengeSkillTag, Long> {
    List<ChallengeSkillTag> findByChallengeId(Long challengeId);
}
