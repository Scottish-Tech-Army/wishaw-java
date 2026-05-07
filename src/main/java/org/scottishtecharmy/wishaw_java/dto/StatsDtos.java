package org.scottishtecharmy.wishaw_java.dto;

import java.util.List;

public final class StatsDtos {

    private StatsDtos() {
    }

    public record PlayerBadgeDto(String id, String name, String icon, String earnedAt) {
    }

    public record PlayerStatsDto(
            int tournamentsJoined,
            int activeTournaments,
            int completedTournaments,
            int matchesPlayed,
            int wins,
            int losses,
            int draws,
            int attendanceRate,
            List<PlayerBadgeDto> badges,
            List<TournamentDtos.TournamentDto> tournaments
    ) {
    }

    public record RegistrationCountDto(String name, int count) {
    }

    public record AttendanceTrendDto(String date, int rate) {
    }

    public record TopPerformerDto(String displayName, int wins) {
    }

    public record RecentScoreDto(String matchLabel, String score, String time) {
    }

    public record AdminDashboardDto(
            int totalTournaments,
            int activeTournaments,
            int totalPlayers,
            int totalMatches,
            List<RegistrationCountDto> registrationsByTournament,
            List<AttendanceTrendDto> attendanceTrend,
            List<TopPerformerDto> topPerformers,
            List<RecentScoreDto> recentScores
    ) {
    }
}
