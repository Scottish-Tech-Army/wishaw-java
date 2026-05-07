package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findAllByOrderByDisplayOrderAsc();
    Level findTopByMinPointsLessThanEqualOrderByMinPointsDesc(int points);
}
