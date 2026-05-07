package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.enums.SportType;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByStatus(TournamentStatus status);
    List<Tournament> findBySportType(SportType sportType);
    List<Tournament> findByCreatedById(Long adminId);
    List<Tournament> findBySportTypeAndStatus(SportType sportType, TournamentStatus status);
}

