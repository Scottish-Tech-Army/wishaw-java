package org.scottishtecharmy.wishaw_java.dto;

import java.util.List;
import java.util.Map;

public final class TournamentDtos {

    private TournamentDtos() {
    }

    public record ScoreFieldDto(String key, String label, String type) {
    }

    public record RankingPointsDto(int win, int draw, int loss) {
    }

    public record SportDto(
            String id,
            String name,
            String icon,
            String description,
            List<ScoreFieldDto> scoreFields,
            RankingPointsDto rankingPoints,
            Integer minAge,
            Integer maxAge
    ) {
    }

    public record SportUpsertRequest(
            String name,
            String icon,
            String description,
            List<ScoreFieldDto> scoreFields,
            RankingPointsDto rankingPoints,
            Integer minAge,
            Integer maxAge
    ) {
    }

    public record TournamentDto(
            String id,
            String name,
            String sportId,
            SportDto sport,
            String description,
            String rules,
            String venue,
            String type,
            String status,
            String startDate,
            String endDate,
            String regStartDate,
            String regEndDate,
            int capacity,
            int participantCount,
            Integer teamMinSize,
            Integer teamMaxSize,
            int pointsWin,
            int pointsDraw,
            int pointsLoss
    ) {
    }

    public record TournamentListResponse(List<TournamentDto> tournaments, int total) {
    }

    public record TournamentUpsertRequest(
            String name,
            String sportId,
            String description,
            String rules,
            String venue,
            String type,
            String status,
            String startDate,
            String endDate,
            String regStartDate,
            String regEndDate,
            Integer capacity,
            Integer teamMinSize,
            Integer teamMaxSize,
            Integer pointsWin,
            Integer pointsDraw,
            Integer pointsLoss
    ) {
    }

    public record ParticipantDto(String id, String userId, String displayName, String status, String photoUrl) {
    }

    public record MatchParticipantDto(String userId, String displayName, String attendance) {
    }

    public record MatchScoreDto(String winnerId, Map<String, Map<String, Object>> fields, String summary) {
    }

    public record MatchDto(
            String id,
            String tournamentId,
            String roundLabel,
            String scheduledAt,
            String venue,
            String status,
            List<MatchParticipantDto> participants,
            MatchScoreDto score
    ) {
    }

    public record MatchUpsertRequest(
            String tournamentId,
            String roundLabel,
            String scheduledAt,
            String venue,
            String status,
            List<MatchParticipantDto> participants
    ) {
    }

    public record ScoreSubmissionRequest(String winnerId, Map<String, Map<String, Object>> fields, String summary) {
    }

    public record AttendanceRecordRequest(String userId, String attendance) {
    }

    public record AttendanceRequest(List<AttendanceRecordRequest> records) {
    }

    public record TeamMemberDto(String userId, String displayName) {
    }

    public record TeamDto(String id, String name, String tournamentId, List<TeamMemberDto> members) {
    }

    public record TeamCreateRequest(String name, String tournamentId) {
    }
}
