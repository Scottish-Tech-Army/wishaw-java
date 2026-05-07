package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.RankingDTO;
import com.ltc.entity.*;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.Ranking;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.RankingRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;

    public List<RankingDTO> getRankingsByTournament(Long tournamentId) {
        return rankingRepository.findByTournamentIdOrderByRankPositionAsc(tournamentId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<RankingDTO> getRankingsByPlayer(Long playerId) {
        return rankingRepository.findByPlayerId(playerId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public RankingDTO getRankingByPlayerAndTournament(Long playerId, Long tournamentId) {
        Ranking ranking = rankingRepository.findByPlayerIdAndTournamentId(playerId, tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ranking not found for player " + playerId + " in tournament " + tournamentId));
        return mapToDTO(ranking);
    }

    @Transactional
    public RankingDTO createOrUpdateRanking(RankingDTO dto) {
        User player = userRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player", dto.getPlayerId()));
        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", dto.getTournamentId()));

        Ranking existing = rankingRepository.findByPlayerIdAndTournamentId(dto.getPlayerId(), dto.getTournamentId())
                .orElse(null);

        if (existing != null) {
            if (dto.getRankPosition() != null) existing.setRankPosition(dto.getRankPosition());
            if (dto.getTotalPoints() != null) existing.setTotalPoints(dto.getTotalPoints());
            if (dto.getWins() != null) existing.setWins(dto.getWins());
            if (dto.getLosses() != null) existing.setLosses(dto.getLosses());
            if (dto.getDraws() != null) existing.setDraws(dto.getDraws());
            Ranking saved = rankingRepository.save(existing);
            return mapToDTO(saved);
        }

        Ranking ranking = Ranking.builder()
                .player(player).tournament(tournament)
                .rankPosition(dto.getRankPosition())
                .totalPoints(dto.getTotalPoints() != null ? dto.getTotalPoints() : 0)
                .wins(dto.getWins() != null ? dto.getWins() : 0)
                .losses(dto.getLosses() != null ? dto.getLosses() : 0)
                .draws(dto.getDraws() != null ? dto.getDraws() : 0)
                .build();
        Ranking saved = rankingRepository.save(ranking);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteRanking(Long id) {
        if (!rankingRepository.existsById(id)) throw new ResourceNotFoundException("Ranking", id);
        rankingRepository.deleteById(id);
    }

    private RankingDTO mapToDTO(Ranking r) {
        return RankingDTO.builder()
                .id(r.getId())
                .playerId(r.getPlayer().getId()).playerName(r.getPlayer().getFullName())
                .tournamentId(r.getTournament().getId()).tournamentName(r.getTournament().getName())
                .rankPosition(r.getRankPosition()).totalPoints(r.getTotalPoints())
                .wins(r.getWins()).losses(r.getLosses()).draws(r.getDraws())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null)
                .updatedAt(r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : null)
                .build();
    }
}

