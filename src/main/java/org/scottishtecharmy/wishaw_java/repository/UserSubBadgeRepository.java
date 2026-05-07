package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.UserSubBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubBadgeRepository extends JpaRepository<UserSubBadge, Long> {
    List<UserSubBadge> findByUserAccount_Id(String userId);
    Optional<UserSubBadge> findByUserAccount_IdAndSubBadge_Id(String userId, String subBadgeId);
    void deleteBySubBadge_IdIn(List<String> subBadgeIds);
}
