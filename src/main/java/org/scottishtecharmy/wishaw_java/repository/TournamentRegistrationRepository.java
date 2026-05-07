package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {
    List<TournamentRegistration> findByTournamentId(Long tournamentId);
    List<TournamentRegistration> findByPlayerId(Long playerId);
    Optional<TournamentRegistration> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    boolean existsByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    long countByTournamentId(Long tournamentId);
}

