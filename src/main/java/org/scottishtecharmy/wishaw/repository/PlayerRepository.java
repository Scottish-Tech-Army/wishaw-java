package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.Player;
import org.scottishtecharmy.wishaw.entity.Centre;
import org.scottishtecharmy.wishaw.entity.Team;
import org.scottishtecharmy.wishaw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByCentre(Centre centre);
    List<Player> findByTeam(Team team);
    Optional<Player> findByUser(User user);

    @Query("SELECT p FROM Player p WHERE p.centre = :centre ORDER BY p.totalXp DESC")
    List<Player> findByCentreOrderByTotalXpDesc(Centre centre);
}
