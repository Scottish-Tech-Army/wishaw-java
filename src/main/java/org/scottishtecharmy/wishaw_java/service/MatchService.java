package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.MatchDTO;
import com.ltc.entity.*;
import org.scottishtecharmy.wishaw_java.entity.Match;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.MatchRepository;
import org.scottishtecharmy.wishaw_java.repository.TeamRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public List<MatchDTO> getMatchesByTournament(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public MatchDTO getMatchById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        return mapToDTO(match);
    }

    public List<MatchDTO> getMatchesByTournamentAndStatus(Long tournamentId, MatchStatus status) {
        return matchRepository.findByTournamentIdAndStatus(tournamentId, status).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByPlayer(Long playerId) {
        return matchRepository.findByPlayerAIdOrPlayerBId(playerId, playerId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByTeam(Long teamId) {
        return matchRepository.findByTeamAIdOrTeamBId(teamId, teamId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByRound(Long tournamentId, Integer roundNumber) {
        return matchRepository.findByTournamentIdAndRoundNumber(tournamentId, roundNumber).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public MatchDTO createMatch(MatchDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", dto.getTournamentId()));

        Match match = Match.builder()
                .tournament(tournament)
                .matchTitle(dto.getMatchTitle())
                .roundNumber(dto.getRoundNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : MatchStatus.SCHEDULED)
                .scheduledTime(dto.getScheduledTime() != null ? LocalDateTime.parse(dto.getScheduledTime()) : null)
                .venue(dto.getVenue())
                .build();

        if (dto.getTeamAId() != null) {
            match.setTeamA(teamRepository.findById(dto.getTeamAId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team A", dto.getTeamAId())));
        }
        if (dto.getTeamBId() != null) {
            match.setTeamB(teamRepository.findById(dto.getTeamBId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team B", dto.getTeamBId())));
        }
        if (dto.getPlayerAId() != null) {
            match.setPlayerA(userRepository.findById(dto.getPlayerAId())
                    .orElseThrow(() -> new ResourceNotFoundException("Player A", dto.getPlayerAId())));
        }
        if (dto.getPlayerBId() != null) {
            match.setPlayerB(userRepository.findById(dto.getPlayerBId())
                    .orElseThrow(() -> new ResourceNotFoundException("Player B", dto.getPlayerBId())));
        }

        Match saved = matchRepository.save(match);
        return mapToDTO(saved);
    }

    @Transactional
    public MatchDTO updateMatch(Long id, MatchDTO dto) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));

        if (dto.getMatchTitle() != null) match.setMatchTitle(dto.getMatchTitle());
        if (dto.getRoundNumber() != null) match.setRoundNumber(dto.getRoundNumber());
        if (dto.getStatus() != null) match.setStatus(dto.getStatus());
        if (dto.getScheduledTime() != null) match.setScheduledTime(LocalDateTime.parse(dto.getScheduledTime()));
        if (dto.getVenue() != null) match.setVenue(dto.getVenue());

        if (dto.getWinnerTeamId() != null) {
            match.setWinnerTeam(teamRepository.findById(dto.getWinnerTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Winner Team", dto.getWinnerTeamId())));
        }
        if (dto.getWinnerPlayerId() != null) {
            match.setWinnerPlayer(userRepository.findById(dto.getWinnerPlayerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Winner Player", dto.getWinnerPlayerId())));
        }

        Match saved = matchRepository.save(match);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Match", id);
        }
        matchRepository.deleteById(id);
    }

    private MatchDTO mapToDTO(Match m) {
        return MatchDTO.builder()
                .id(m.getId())
                .tournamentId(m.getTournament().getId())
                .tournamentName(m.getTournament().getName())
                .matchTitle(m.getMatchTitle())
                .roundNumber(m.getRoundNumber())
                .teamAId(m.getTeamA() != null ? m.getTeamA().getId() : null)
                .teamAName(m.getTeamA() != null ? m.getTeamA().getName() : null)
                .teamBId(m.getTeamB() != null ? m.getTeamB().getId() : null)
                .teamBName(m.getTeamB() != null ? m.getTeamB().getName() : null)
                .playerAId(m.getPlayerA() != null ? m.getPlayerA().getId() : null)
                .playerAName(m.getPlayerA() != null ? m.getPlayerA().getFullName() : null)
                .playerBId(m.getPlayerB() != null ? m.getPlayerB().getId() : null)
                .playerBName(m.getPlayerB() != null ? m.getPlayerB().getFullName() : null)
                .status(m.getStatus())
                .scheduledTime(m.getScheduledTime() != null ? m.getScheduledTime().toString() : null)
                .venue(m.getVenue())
                .winnerTeamId(m.getWinnerTeam() != null ? m.getWinnerTeam().getId() : null)
                .winnerTeamName(m.getWinnerTeam() != null ? m.getWinnerTeam().getName() : null)
                .winnerPlayerId(m.getWinnerPlayer() != null ? m.getWinnerPlayer().getId() : null)
                .winnerPlayerName(m.getWinnerPlayer() != null ? m.getWinnerPlayer().getFullName() : null)
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null)
                .updatedAt(m.getUpdatedAt() != null ? m.getUpdatedAt().toString() : null)
                .build();
    }
}

