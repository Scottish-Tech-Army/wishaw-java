package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.CalorieRecordDTO;
import com.ltc.entity.*;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.CalorieRecord;
import org.scottishtecharmy.wishaw_java.entity.Match;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.CalorieRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalorieService {

    private final CalorieRecordRepository calorieRecordRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public List<CalorieRecordDTO> getCaloriesByPlayer(Long playerId) {
        return calorieRecordRepository.findByPlayerId(playerId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<CalorieRecordDTO> getCaloriesByMatch(Long matchId) {
        return calorieRecordRepository.findByMatchId(matchId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<CalorieRecordDTO> getCaloriesByTournament(Long tournamentId) {
        return calorieRecordRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public Double getTotalCaloriesByPlayer(Long playerId) {
        Double total = calorieRecordRepository.sumCaloriesByPlayerId(playerId);
        return total != null ? total : 0.0;
    }

    public Double getTotalCaloriesByPlayerAndTournament(Long playerId, Long tournamentId) {
        Double total = calorieRecordRepository.sumCaloriesByPlayerIdAndTournamentId(playerId, tournamentId);
        return total != null ? total : 0.0;
    }

    @Transactional
    public CalorieRecordDTO addCalorieRecord(CalorieRecordDTO dto, Long adminId) {
        User player = userRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player", dto.getPlayerId()));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        CalorieRecord record = CalorieRecord.builder()
                .player(player).caloriesBurned(dto.getCaloriesBurned()).enteredBy(admin).build();

        if (dto.getMatchId() != null) {
            Match match = matchRepository.findById(dto.getMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Match", dto.getMatchId()));
            record.setMatch(match);
        }
        if (dto.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tournament", dto.getTournamentId()));
            record.setTournament(tournament);
        }

        CalorieRecord saved = calorieRecordRepository.save(record);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteCalorieRecord(Long id) {
        if (!calorieRecordRepository.existsById(id)) throw new ResourceNotFoundException("CalorieRecord", id);
        calorieRecordRepository.deleteById(id);
    }

    private CalorieRecordDTO mapToDTO(CalorieRecord c) {
        return CalorieRecordDTO.builder()
                .id(c.getId())
                .playerId(c.getPlayer().getId()).playerName(c.getPlayer().getFullName())
                .matchId(c.getMatch() != null ? c.getMatch().getId() : null)
                .matchTitle(c.getMatch() != null ? c.getMatch().getMatchTitle() : null)
                .tournamentId(c.getTournament() != null ? c.getTournament().getId() : null)
                .tournamentName(c.getTournament() != null ? c.getTournament().getName() : null)
                .caloriesBurned(c.getCaloriesBurned())
                .enteredById(c.getEnteredBy() != null ? c.getEnteredBy().getId() : null)
                .enteredByName(c.getEnteredBy() != null ? c.getEnteredBy().getFullName() : null)
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .build();
    }
}

