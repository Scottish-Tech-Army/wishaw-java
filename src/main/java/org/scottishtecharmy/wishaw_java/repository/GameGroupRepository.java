package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.GameGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameGroupRepository extends JpaRepository<GameGroup, String> {
    List<GameGroup> findByCentre_IdOrderByNameAsc(String centreId);
}
