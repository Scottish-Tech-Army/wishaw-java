package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.Team;
import org.scottishtecharmy.wishaw.entity.Centre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByCentre(Centre centre);
}
