package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.LegacyPoint;
import org.scottishtecharmy.wishaw.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LegacyPointRepository extends JpaRepository<LegacyPoint, Long> {
    List<LegacyPoint> findByPlayer(Player player);
}
