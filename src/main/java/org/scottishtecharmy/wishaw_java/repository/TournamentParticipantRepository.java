package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.TournamentParticipant;
import org.scottishtecharmy.wishaw_java.enums.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
    List<TournamentParticipant> findByTournament_IdOrderByIdAsc(String tournamentId);
    Optional<TournamentParticipant> findByTournament_IdAndUserAccount_Id(String tournamentId, String userId);
    long countByTournament_IdAndStatus(String tournamentId, ParticipantStatus status);
}
