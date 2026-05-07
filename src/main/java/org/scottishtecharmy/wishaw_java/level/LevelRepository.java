package org.scottishtecharmy.wishaw_java.level;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByName(String name);
    List<Level> findAllByOrderByDisplayOrderAsc();
}

