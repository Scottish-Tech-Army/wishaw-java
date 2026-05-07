package org.scottishtecharmy.wishaw_java.service;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.AdminDashboardDTO;
import org.scottishtecharmy.wishaw_java.dto.PlayerBadgeDTO;
import org.scottishtecharmy.wishaw_java.dto.PlayerStatsDTO;
import org.scottishtecharmy.wishaw_java.dto.RankingDTO;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;
    private final AttendanceRepository attendanceRepository;
    private final CalorieRecordRepository calorieRecordRepository;
    private final BadgeService badgeService;
    private final RankingService rankingService;

    public PlayerStatsDTO getPlayerStats(Long playerId) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", playerId));

        long presentCount = attendanceRepository.countByPlayerIdAndStatus(playerId, AttendanceStatus.PRESENT);
        long absentCount = attendanceRepository.countByPlayerIdAndStatus(playerId, AttendanceStatus.ABSENT);
        long lateCount = attendanceRepository.countByPlayerIdAndStatus(playerId, AttendanceStatus.LATE);
        long excusedCount = attendanceRepository.countByPlayerIdAndStatus(playerId, AttendanceStatus.EXCUSED);
        long totalAttendance = presentCount + absentCount + lateCount + excusedCount;
        double attendancePercentage = totalAttendance > 0
                ? ((double) (presentCount + lateCount) / totalAttendance) * 100.0
                : 0.0;

        Double totalCalories = calorieRecordRepository.sumCaloriesByPlayerId(playerId);

        var playerMatches = matchRepository.findByPlayerAIdOrPlayerBId(playerId, playerId);
        int wins = 0, losses = 0, draws = 0;
        for (var match : playerMatches) {
            if (match.getStatus() == MatchStatus.COMPLETED) {
                if (match.getWinnerPlayer() != null) {
                    if (match.getWinnerPlayer().getId().equals(playerId)) {
                        wins++;
                    } else {
                        losses++;
                    }
                } else {
                    draws++;
                }
            }
        }

        List<PlayerBadgeDTO> badges = badgeService.getBadgesByPlayer(playerId);
        List<RankingDTO> rankings = rankingService.getRankingsByPlayer(playerId);

        return PlayerStatsDTO.builder()
                .playerId(player.getId())
                .playerName(player.getFullName())
                .totalMatches(playerMatches.size())
                .wins(wins).losses(losses).draws(draws)
                .totalCalories(totalCalories != null ? totalCalories : 0.0)
                .presentCount(presentCount).absentCount(absentCount)
                .lateCount(lateCount).excusedCount(excusedCount)
                .attendancePercentage(Math.round(attendancePercentage * 100.0) / 100.0)
                .badges(badges).rankings(rankings)
                .build();
    }

    public AdminDashboardDTO getAdminDashboard() {
        long totalTournaments = tournamentRepository.count();
        long activeTournaments = tournamentRepository.findByStatus(TournamentStatus.IN_PROGRESS).size();
        long totalPlayers = userRepository.findByRole(UserRole.PLAYER).size();
        long totalMatches = matchRepository.count();
        long completedMatches = matchRepository.findAll().stream()
                .filter(m -> m.getStatus() == MatchStatus.COMPLETED).count();
        long upcomingMatches = matchRepository.findAll().stream()
                .filter(m -> m.getStatus() == MatchStatus.SCHEDULED).count();
        long totalRegistrations = registrationRepository.count();
        long totalTeams = teamRepository.count();

        return AdminDashboardDTO.builder()
                .totalTournaments(totalTournaments)
                .activeTournaments(activeTournaments)
                .totalPlayers(totalPlayers)
                .totalMatches(totalMatches)
                .completedMatches(completedMatches)
                .upcomingMatches(upcomingMatches)
                .totalRegistrations(totalRegistrations)
                .totalTeams(totalTeams)
                .build();
    }
}

