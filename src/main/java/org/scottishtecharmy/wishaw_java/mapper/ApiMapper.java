package org.scottishtecharmy.wishaw_java.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scottishtecharmy.wishaw_java.dto.AuthDtos;
import org.scottishtecharmy.wishaw_java.dto.CatalogueDtos;
import org.scottishtecharmy.wishaw_java.dto.NotificationDtos;
import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.GameGroup;
import org.scottishtecharmy.wishaw_java.entity.LearningModule;
import org.scottishtecharmy.wishaw_java.entity.MainBadge;
import org.scottishtecharmy.wishaw_java.entity.MatchParticipant;
import org.scottishtecharmy.wishaw_java.entity.MatchRecord;
import org.scottishtecharmy.wishaw_java.entity.ModuleSessionItem;
import org.scottishtecharmy.wishaw_java.entity.NotificationRecord;
import org.scottishtecharmy.wishaw_java.entity.Sport;
import org.scottishtecharmy.wishaw_java.entity.SubBadge;
import org.scottishtecharmy.wishaw_java.entity.Team;
import org.scottishtecharmy.wishaw_java.entity.TeamMember;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.TournamentParticipant;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ApiMapper {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<TournamentDtos.ScoreFieldDto>> SCORE_FIELD_LIST = new TypeReference<>() {
    };
    private static final TypeReference<Map<String, Map<String, Object>>> SCORE_MAP = new TypeReference<>() {
    };
    private static final DateTimeFormatter ISO_INSTANT = DateTimeFormatter.ISO_INSTANT;
    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final ObjectMapper objectMapper;

    public ApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuthDtos.UserDto toUserDto(UserAccount userAccount) {
        return new AuthDtos.UserDto(
                userAccount.getId(),
                userAccount.getEmail(),
                userAccount.getRole().name(),
                userAccount.getCentre() == null ? null : userAccount.getCentre().getId()
        );
    }

    public AuthDtos.ProfileDto toProfileDto(UserProfile userProfile) {
        return new AuthDtos.ProfileDto(
                userProfile.getDisplayName(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
            formatDate(userProfile.getDateOfBirth()),
                userProfile.getBio(),
                userProfile.getPhotoUrl(),
                userProfile.getOverlayTemplate(),
                new AuthDtos.PrivacyDto(userProfile.isShowInPublicList(), userProfile.isAllowSocialSharing())
        );
    }

    public CatalogueDtos.CentreDto toCentreDto(Centre centre) {
        return new CatalogueDtos.CentreDto(centre.getId(), centre.getName(), centre.getLocation());
    }

    public CatalogueDtos.GroupDto toGroupDto(GameGroup gameGroup) {
        return new CatalogueDtos.GroupDto(
                gameGroup.getId(),
                gameGroup.getName(),
                gameGroup.getGame(),
                gameGroup.getCentre().getId(),
                gameGroup.getCentre().getName(),
                gameGroup.getMemberCount()
        );
    }

    public CatalogueDtos.MainBadgeDto toMainBadgeDto(MainBadge mainBadge) {
        return new CatalogueDtos.MainBadgeDto(mainBadge.getId(), mainBadge.getName(), mainBadge.getDescription(), mainBadge.getIcon());
    }

    public CatalogueDtos.SubBadgeDto toSubBadgeDto(SubBadge subBadge) {
        return new CatalogueDtos.SubBadgeDto(
                subBadge.getId(),
                subBadge.getName(),
                subBadge.getDescription(),
                subBadge.getMainBadge().getId(),
                subBadge.getMainBadge().getName(),
                subBadge.getPoints(),
                readStringList(subBadge.getSkillsCsv()),
                subBadge.getLearningModule() == null ? null : subBadge.getLearningModule().getId()
        );
    }

    public CatalogueDtos.ModuleSessionDto toModuleSessionDto(ModuleSessionItem sessionItem) {
        return new CatalogueDtos.ModuleSessionDto(
                sessionItem.getWeekNo(),
                sessionItem.getFocus(),
                sessionItem.getSubBadge() == null ? null : sessionItem.getSubBadge().getId(),
                sessionItem.getSessionPlanUrl(),
                sessionItem.getSlidesUrl()
        );
    }

    public CatalogueDtos.ModuleDto toModuleDto(
            LearningModule learningModule,
            List<CatalogueDtos.SubBadgeDto> subBadges,
            List<CatalogueDtos.ModuleSessionDto> schedule
    ) {
        return new CatalogueDtos.ModuleDto(
                learningModule.getId(),
                learningModule.getName(),
                learningModule.getGame(),
                learningModule.getDescription(),
                learningModule.getDurationWeeks(),
                subBadges,
                schedule,
                learningModule.getStatus().name()
        );
    }

    public TournamentDtos.SportDto toSportDto(Sport sport) {
        return new TournamentDtos.SportDto(
                sport.getId(),
                sport.getName(),
                sport.getIcon(),
                sport.getDescription(),
                readScoreFields(sport.getScoreFieldsJson()),
                new TournamentDtos.RankingPointsDto(sport.getRankingWin(), sport.getRankingDraw(), sport.getRankingLoss()),
                sport.getMinAge(),
                sport.getMaxAge()
        );
    }

    public TournamentDtos.TournamentDto toTournamentDto(Tournament tournament) {
        TournamentDtos.SportDto sportDto = tournament.getSport() == null ? null : toSportDto(tournament.getSport());
        return new TournamentDtos.TournamentDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getSport() == null ? null : tournament.getSport().getId(),
                sportDto,
                tournament.getDescription(),
                tournament.getRulesText(),
                tournament.getVenue(),
                tournament.getType().name(),
                tournament.getStatus().name(),
                formatDateTime(tournament.getStartDate()),
                formatDateTime(tournament.getEndDate()),
                formatDateTime(tournament.getRegStartDate()),
                formatDateTime(tournament.getRegEndDate()),
                tournament.getCapacity(),
                tournament.getParticipantCount(),
                tournament.getTeamMinSize(),
                tournament.getTeamMaxSize(),
                tournament.getPointsWin(),
                tournament.getPointsDraw(),
                tournament.getPointsLoss()
        );
    }

    public TournamentDtos.ParticipantDto toParticipantDto(TournamentParticipant participant, String photoUrl) {
        return new TournamentDtos.ParticipantDto(
                String.valueOf(participant.getId()),
                participant.getUserAccount().getId(),
                participant.getDisplayNameSnapshot(),
                participant.getStatus().name(),
                photoUrl
        );
    }

    public TournamentDtos.MatchParticipantDto toMatchParticipantDto(MatchParticipant participant) {
        return new TournamentDtos.MatchParticipantDto(
                participant.getUserAccount().getId(),
                participant.getDisplayNameSnapshot(),
                participant.getAttendance() == null ? null : participant.getAttendance().name()
        );
    }

    public TournamentDtos.MatchParticipantDto toMatchParticipantDto(TeamMember teamMember) {
        return new TournamentDtos.MatchParticipantDto(
                teamMember.getUserAccount().getId(),
                teamMember.getDisplayNameSnapshot(),
                null
        );
    }

    public TournamentDtos.MatchScoreDto toMatchScoreDto(MatchRecord matchRecord) {
        if (matchRecord.getScoreJson() == null || matchRecord.getScoreJson().isBlank()) {
            return null;
        }
        return new TournamentDtos.MatchScoreDto(
                matchRecord.getWinnerUserId(),
                readScoreMap(matchRecord.getScoreJson()),
                matchRecord.getScoreSummary()
        );
    }

    public TournamentDtos.MatchDto toMatchDto(MatchRecord matchRecord, List<TournamentDtos.MatchParticipantDto> participants) {
        return new TournamentDtos.MatchDto(
                matchRecord.getId(),
                matchRecord.getTournament().getId(),
                matchRecord.getRoundLabel(),
                formatDateTime(matchRecord.getScheduledAt()),
                matchRecord.getVenue(),
                matchRecord.getStatus().name(),
                participants,
                toMatchScoreDto(matchRecord)
        );
    }

    public TournamentDtos.TeamDto toTeamDto(Team team, List<TournamentDtos.TeamMemberDto> members) {
        return new TournamentDtos.TeamDto(team.getId(), team.getName(), team.getTournament().getId(), members);
    }

    public TournamentDtos.TeamMemberDto toTeamMemberDto(TeamMember teamMember) {
        return new TournamentDtos.TeamMemberDto(teamMember.getUserAccount().getId(), teamMember.getDisplayNameSnapshot());
    }

    public NotificationDtos.NotificationDto toNotificationDto(NotificationRecord notificationRecord) {
        return new NotificationDtos.NotificationDto(
                notificationRecord.getId(),
                notificationRecord.getType().name(),
                notificationRecord.getTitle(),
                notificationRecord.getMessage(),
                notificationRecord.isRead(),
                ISO_INSTANT.format(notificationRecord.getCreatedAt()),
                notificationRecord.getLinkTo()
        );
    }

    public String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize JSON payload", exception);
        }
    }

    public List<String> readStringList(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        return readValue(rawValue, STRING_LIST, List.of());
    }

    public List<TournamentDtos.ScoreFieldDto> readScoreFields(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        return readValue(rawValue, SCORE_FIELD_LIST, List.of());
    }

    public String formatDate(LocalDate value) {
        return value == null ? null : value.toString();
    }

    public Map<String, Map<String, Object>> readScoreMap(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Collections.emptyMap();
        }
        return readValue(rawValue, SCORE_MAP, Collections.emptyMap());
    }

    public String formatDateTime(java.time.LocalDateTime value) {
        return value == null ? null : ISO_LOCAL.format(value.atOffset(ZoneOffset.UTC));
    }

    private <T> T readValue(String rawValue, TypeReference<T> typeReference, T fallback) {
        try {
            return objectMapper.readValue(rawValue, typeReference);
        } catch (Exception exception) {
            return fallback;
        }
    }
}
