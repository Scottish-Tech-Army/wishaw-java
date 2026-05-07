package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.ScoreAuditLogDTO;
import org.scottishtecharmy.wishaw_java.dto.ScoreDTO;
import com.ltc.entity.*;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.Match;
import org.scottishtecharmy.wishaw_java.entity.Score;
import org.scottishtecharmy.wishaw_java.entity.ScoreAuditLog;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final ScoreAuditLogRepository auditLogRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public List<ScoreDTO> getScoresByMatch(Long matchId) {
        return scoreRepository.findByMatchId(matchId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ScoreDTO> getScoresByPlayer(Long playerId) {
        return scoreRepository.findByPlayerId(playerId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ScoreDTO> getScoresByTeam(Long teamId) {
        return scoreRepository.findByTeamId(teamId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ScoreDTO createOrUpdateScore(ScoreDTO dto, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user", adminId));

        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match", dto.getMatchId()));

        Score existingScore = null;

        if (dto.getPlayerId() != null) {
            existingScore = scoreRepository.findByMatchIdAndPlayerId(dto.getMatchId(), dto.getPlayerId()).orElse(null);
        } else if (dto.getTeamId() != null) {
            existingScore = scoreRepository.findByMatchIdAndTeamId(dto.getMatchId(), dto.getTeamId()).orElse(null);
        }

        if (existingScore != null) {
            ScoreAuditLog auditLog = ScoreAuditLog.builder()
                    .score(existingScore)
                    .match(match)
                    .previousScore(existingScore.getScoreValue())
                    .newScore(dto.getScoreValue())
                    .previousDetails(existingScore.getScoreDetails())
                    .newDetails(dto.getScoreDetails())
                    .editedBy(admin)
                    .build();
            auditLogRepository.save(auditLog);

            existingScore.setScoreValue(dto.getScoreValue());
            existingScore.setScoreDetails(dto.getScoreDetails());
            existingScore.setUpdatedBy(admin);

            Score saved = scoreRepository.save(existingScore);
            return mapToDTO(saved);
        } else {
            Score score = Score.builder()
                    .match(match)
                    .scoreValue(dto.getScoreValue())
                    .scoreDetails(dto.getScoreDetails())
                    .updatedBy(admin)
                    .build();

            if (dto.getPlayerId() != null) {
                score.setPlayer(userRepository.findById(dto.getPlayerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Player", dto.getPlayerId())));
            }
            if (dto.getTeamId() != null) {
                score.setTeam(teamRepository.findById(dto.getTeamId())
                        .orElseThrow(() -> new ResourceNotFoundException("Team", dto.getTeamId())));
            }

            Score saved = scoreRepository.save(score);

            ScoreAuditLog auditLog = ScoreAuditLog.builder()
                    .score(saved).match(match)
                    .previousScore(0).newScore(dto.getScoreValue())
                    .previousDetails(null).newDetails(dto.getScoreDetails())
                    .editedBy(admin).build();
            auditLogRepository.save(auditLog);

            return mapToDTO(saved);
        }
    }

    public List<ScoreAuditLogDTO> getAuditLogsByScore(Long scoreId) {
        return auditLogRepository.findByScoreId(scoreId).stream()
                .map(this::mapAuditToDTO).collect(Collectors.toList());
    }

    public List<ScoreAuditLogDTO> getAuditLogsByMatch(Long matchId) {
        return auditLogRepository.findByMatchId(matchId).stream()
                .map(this::mapAuditToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteScore(Long id) {
        if (!scoreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Score", id);
        }
        scoreRepository.deleteById(id);
    }

    private ScoreDTO mapToDTO(Score s) {
        return ScoreDTO.builder()
                .id(s.getId()).matchId(s.getMatch().getId())
                .playerId(s.getPlayer() != null ? s.getPlayer().getId() : null)
                .playerName(s.getPlayer() != null ? s.getPlayer().getFullName() : null)
                .teamId(s.getTeam() != null ? s.getTeam().getId() : null)
                .teamName(s.getTeam() != null ? s.getTeam().getName() : null)
                .scoreValue(s.getScoreValue()).scoreDetails(s.getScoreDetails())
                .updatedById(s.getUpdatedBy() != null ? s.getUpdatedBy().getId() : null)
                .updatedByName(s.getUpdatedBy() != null ? s.getUpdatedBy().getFullName() : null)
                .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                .updatedAt(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null)
                .build();
    }

    private ScoreAuditLogDTO mapAuditToDTO(ScoreAuditLog a) {
        return ScoreAuditLogDTO.builder()
                .id(a.getId()).scoreId(a.getScore().getId()).matchId(a.getMatch().getId())
                .previousScore(a.getPreviousScore()).newScore(a.getNewScore())
                .previousDetails(a.getPreviousDetails()).newDetails(a.getNewDetails())
                .editedById(a.getEditedBy().getId())
                .editedByName(a.getEditedBy().getFullName())
                .editedAt(a.getEditedAt() != null ? a.getEditedAt().toString() : null)
                .build();
    }
}

