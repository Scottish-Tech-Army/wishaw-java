package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ParentLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentLinkRepository extends JpaRepository<ParentLink, Long> {
    List<ParentLink> findByParentUserId(Long parentUserId);
    List<ParentLink> findByPlayerUserId(Long playerUserId);
    boolean existsByParentUserIdAndPlayerUserId(Long parentUserId, Long playerUserId);
    void deleteByParentUserIdOrPlayerUserId(Long parentUserId, Long playerUserId);
}
