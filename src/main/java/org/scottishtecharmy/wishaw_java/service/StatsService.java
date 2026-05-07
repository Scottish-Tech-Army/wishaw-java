package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.StatsDtos;
import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.entity.MatchParticipant;
import org.scottishtecharmy.wishaw_java.entity.MatchRecord;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.TournamentParticipant;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.entity.UserSubBadge;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import org.scottishtecharmy.wishaw_java.enums.ParticipantStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.MatchParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.repository.UserProfileRepository;
import org.scottishtecharmy.wishaw_java.repository.UserSubBadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final UserSubBadgeRepository userSubBadgeRepository;
    private final ApiMapper apiMapper;

    public StatsService(UserAccountRepository userAccountRepository,
                        UserProfileRepository userProfileRepository,
                        TournamentParticipantRepository tournamentParticipantRepository,
                        TournamentRepository tournamentRepository,
                        MatchParticipantRepository matchParticipantRepository,
                        MatchRecordRepository matchRecordRepository,
                        UserSubBadgeRepository userSubBadgeRepository,
                        ApiMapper apiMapper) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.tournamentRepository = tournamentRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.userSubBadgeRepository = userSubBadgeRepository;
        this.apiMapper = apiMapper;
    }

    public StatsDtos.PlayerStatsDto getPlayerStats(String userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<TournamentParticipant> participants = tournamentParticipantRepository.findAll().stream()
                .filter(item -> item.getUserAccount().getId().equals(userId) && item.getStatus() == ParticipantStatus.REGISTERED)
                .toList();
        List<Tournament> tournaments = participants.stream().map(TournamentParticipant::getTournament).distinct().toList();
        List<TournamentDtos.TournamentDto> tournamentDtos = tournaments.stream().map(apiMapper::toTournamentDto).toList();

        List<MatchParticipant> matchesForUser = matchParticipantRepository.findAll().stream()
                .filter(item -> item.getUserAccount().getId().equals(userId))
                .toList();
        Map<String, MatchRecord> matchById = matchRecordRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(MatchRecord::getId, item -> item));

        int matchesPlayed = matchesForUser.size();
        int wins = 0;
        int losses = 0;
        int draws = 0;
        int attended = 0;
        for (MatchParticipant participant : matchesForUser) {
            MatchRecord matchRecord = matchById.get(participant.getMatchRecord().getId());
            if (participant.getAttendance() == AttendanceStatus.PRESENT || participant.getAttendance() == AttendanceStatus.LATE || participant.getAttendance() == AttendanceStatus.EXCUSED) {
                attended++;
            }
            if (matchRecord != null && matchRecord.getStatus() == MatchStatus.COMPLETED) {
                if (matchRecord.getWinnerUserId() == null || matchRecord.getWinnerUserId().isBlank()) {
                    draws++;
                } else if (matchRecord.getWinnerUserId().equals(userId)) {
                    wins++;
                } else {
                    losses++;
                }
            }
        }

        int attendanceRate = matchesPlayed == 0 ? 100 : (int) Math.round((attended * 100.0) / matchesPlayed);
        List<UserSubBadge> awards = userSubBadgeRepository.findByUserAccount_Id(userId);
        List<StatsDtos.PlayerBadgeDto> badges = awards.stream()
                .collect(java.util.stream.Collectors.toMap(
                        award -> award.getSubBadge().getMainBadge().getId(),
                        award -> award,
                        (left, right) -> left.getAwardedAt().isBefore(right.getAwardedAt()) ? left : right,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(award -> new StatsDtos.PlayerBadgeDto(
                        award.getSubBadge().getMainBadge().getId(),
                        award.getSubBadge().getMainBadge().getName(),
                        award.getSubBadge().getMainBadge().getIcon(),
                        award.getAwardedAt().toString()
                ))
                .toList();

        int activeTournaments = (int) tournaments.stream().filter(tournament -> tournament.getStatus() == TournamentStatus.PUBLISHED).count();
        int completedTournaments = (int) tournaments.stream().filter(tournament -> tournament.getStatus() == TournamentStatus.COMPLETED).count();

        return new StatsDtos.PlayerStatsDto(
                tournaments.size(),
                activeTournaments,
                completedTournaments,
                matchesPlayed,
                wins,
                losses,
                draws,
                attendanceRate,
                badges,
                tournamentDtos
        );
    }

    public StatsDtos.AdminDashboardDto getAdminDashboard() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        List<TournamentParticipant> participants = tournamentParticipantRepository.findAll();
        List<MatchRecord> matches = matchRecordRepository.findAll();
        List<UserProfile> profiles = userProfileRepository.findAll();

        List<StatsDtos.RegistrationCountDto> registrationsByTournament = tournaments.stream()
                .map(tournament -> new StatsDtos.RegistrationCountDto(
                        tournament.getName(),
                        (int) participants.stream()
                                .filter(participant -> participant.getTournament().getId().equals(tournament.getId()) && participant.getStatus() == ParticipantStatus.REGISTERED)
                                .count()))
                .toList();

        List<StatsDtos.AttendanceTrendDto> attendanceTrend = matches.stream()
                .sorted(Comparator.comparing(MatchRecord::getScheduledAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(3)
                .map(match -> {
                    List<MatchParticipant> matchParticipants = matchParticipantRepository.findByMatchRecord_IdOrderByIdAsc(match.getId());
                    long presentCount = matchParticipants.stream()
                            .filter(item -> item.getAttendance() == AttendanceStatus.PRESENT || item.getAttendance() == AttendanceStatus.LATE || item.getAttendance() == AttendanceStatus.EXCUSED)
                            .count();
                    int rate = matchParticipants.isEmpty() ? 0 : (int) Math.round((presentCount * 100.0) / matchParticipants.size());
                    return new StatsDtos.AttendanceTrendDto(match.getRoundLabel(), rate);
                })
                .toList();

        Map<String, Integer> winsByUser = new LinkedHashMap<>();
        for (MatchRecord match : matches) {
            if (match.getWinnerUserId() != null && !match.getWinnerUserId().isBlank()) {
                winsByUser.merge(match.getWinnerUserId(), 1, Integer::sum);
            }
        }
        List<StatsDtos.TopPerformerDto> topPerformers = winsByUser.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new StatsDtos.TopPerformerDto(
                        userProfileRepository.findById(entry.getKey()).map(UserProfile::getDisplayName).orElse(entry.getKey()),
                        entry.getValue()))
                .toList();

        Instant now = Instant.now();
        List<StatsDtos.RecentScoreDto> recentScores = matches.stream()
                .filter(match -> match.getScoreSummary() != null && !match.getScoreSummary().isBlank())
                .sorted(Comparator.comparing(MatchRecord::getScheduledAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(match -> new StatsDtos.RecentScoreDto(
                        match.getRoundLabel(),
                        match.getScoreSummary(),
                        match.getScheduledAt() == null ? "just now" : formatRelative(match.getScheduledAt().toInstant(ZoneOffset.UTC), now)))
                .toList();

        return new StatsDtos.AdminDashboardDto(
                tournaments.size(),
                (int) tournaments.stream().filter(tournament -> tournament.getStatus() == TournamentStatus.PUBLISHED).count(),
                profiles.size(),
                matches.size(),
                registrationsByTournament,
                attendanceTrend,
                topPerformers,
                recentScores
        );
    }

    private String formatRelative(Instant value, Instant now) {
        long hours = Math.max(0, Duration.between(value, now).toHours());
        if (hours < 24) {
            return hours + "h ago";
        }
        return (hours / 24) + "d ago";
    }
}
