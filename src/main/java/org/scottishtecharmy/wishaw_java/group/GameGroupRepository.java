package org.scottishtecharmy.wishaw_java.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameGroupRepository extends JpaRepository<GameGroup, Long> {
    List<GameGroup> findByCentreId(Long centreId);
}
