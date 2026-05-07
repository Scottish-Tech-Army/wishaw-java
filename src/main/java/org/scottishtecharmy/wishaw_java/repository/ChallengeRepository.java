package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);
    boolean existsByModuleIdAndName(Long moduleId, String name);
}
