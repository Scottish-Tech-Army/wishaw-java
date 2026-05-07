package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.entity.MatchParticipant;
import org.scottishtecharmy.wishaw_java.entity.MatchRecord;
import org.scottishtecharmy.wishaw_java.entity.Team;
import org.scottishtecharmy.wishaw_java.entity.TeamMember;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.TournamentParticipant;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import org.scottishtecharmy.wishaw_java.enums.ParticipantStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentType;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.MatchParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.SportRepository;
import org.scottishtecharmy.wishaw_java.repository.TeamMemberRepository;
import org.scottishtecharmy.wishaw_java.repository.TeamRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final SportRepository sportRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final ApiMapper apiMapper;
    private final AgePolicyService agePolicyService;
    private final NotificationService notificationService;

    public TournamentService(TournamentRepository tournamentRepository,
                             SportRepository sportRepository,
                             TournamentParticipantRepository tournamentParticipantRepository,
                             MatchRecordRepository matchRecordRepository,
                             MatchParticipantRepository matchParticipantRepository,
                             TeamRepository teamRepository,
                             TeamMemberRepository teamMemberRepository,
                             UserAccountRepository userAccountRepository,
                             UserProfileRepository userProfileRepository,
                             ApiMapper apiMapper,
                             AgePolicyService agePolicyService,
                             NotificationService notificationService) {
        this.tournamentRepository = tournamentRepository;
        this.sportRepository = sportRepository;
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.apiMapper = apiMapper;
        this.agePolicyService = agePolicyService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public TournamentDtos.TournamentListResponse getTournaments() {
        List<TournamentDtos.TournamentDto> tournaments = tournamentRepository.findAll().stream()
                .map(apiMapper::toTournamentDto)
                .toList();
        return new TournamentDtos.TournamentListResponse(tournaments, tournaments.size());
    }

    @Transactional(readOnly = true)
    public TournamentDtos.TournamentDto getTournament(String id) {
        return apiMapper.toTournamentDto(requireTournament(id));
    }

    public TournamentDtos.TournamentDto createTournament(TournamentDtos.TournamentUpsertRequest request) {
        Tournament tournament = new Tournament();
        tournament.setId("t" + System.currentTimeMillis());
        applyTournamentUpdate(tournament, request);
        return apiMapper.toTournamentDto(tournamentRepository.save(tournament));
    }

    public TournamentDtos.TournamentDto updateTournament(String id, TournamentDtos.TournamentUpsertRequest request) {
        Tournament tournament = requireTournament(id);
        applyTournamentUpdate(tournament, request);
        return apiMapper.toTournamentDto(tournamentRepository.save(tournament));
    }

    public TournamentDtos.TournamentDto changeStatus(String id, TournamentStatus status) {
        Tournament tournament = requireTournament(id);
        TournamentStatus currentStatus = tournament.getStatus();

        if (currentStatus == status) {
            return apiMapper.toTournamentDto(tournament);
        }

        if (status == TournamentStatus.PUBLISHED && currentStatus != TournamentStatus.DRAFT) {
            throw new BadRequestException("Only draft tournaments can be published");
        }

        if (status == TournamentStatus.COMPLETED && currentStatus != TournamentStatus.PUBLISHED) {
            throw new BadRequestException("Only published tournaments can be completed");
        }

        tournament.setStatus(status);
        Tournament savedTournament = tournamentRepository.save(tournament);

        if (status == TournamentStatus.PUBLISHED) {
            notificationService.notifyTournamentPublished(savedTournament);
        }

        return apiMapper.toTournamentDto(savedTournament);
    }

    public Map<String, Boolean> joinTournament(String id, UserAccount currentUser) {
        Tournament tournament = requireTournament(id);
        if (tournament.getStatus() != TournamentStatus.PUBLISHED) {
            throw new BadRequestException("Tournament is not open for registration");
        }
        UserProfile userProfile = userProfileRepository.findById(currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        String displayName = userProfile.getDisplayName() == null || userProfile.getDisplayName().isBlank()
            ? currentUser.getEmail()
            : userProfile.getDisplayName();

        TournamentParticipant existingParticipant = tournamentParticipantRepository.findByTournament_IdAndUserAccount_Id(id, currentUser.getId())
            .orElse(null);
        if (existingParticipant != null && existingParticipant.getStatus() == ParticipantStatus.REGISTERED) {
            return Map.of("success", true);
        }

        LocalDate referenceDate = tournament.getStartDate() == null
            ? LocalDate.now(ZoneOffset.UTC)
            : tournament.getStartDate().toLocalDate();
        agePolicyService.validateSportEligibility(tournament.getSport(), userProfile, referenceDate);

        boolean notifyAdmins = true;
        if (existingParticipant != null) {
            existingParticipant.setDisplayNameSnapshot(displayName);
            existingParticipant.setStatus(ParticipantStatus.REGISTERED);
            tournamentParticipantRepository.save(existingParticipant);
        } else {
            tournamentParticipantRepository.save(TournamentParticipant.builder()
                .tournament(tournament)
                .userAccount(currentUser)
                .displayNameSnapshot(displayName)
                .status(ParticipantStatus.REGISTERED)
                .build());
        }
        tournament.setParticipantCount((int) tournamentParticipantRepository.countByTournament_IdAndStatus(id, ParticipantStatus.REGISTERED));
        tournamentRepository.save(tournament);

        if (notifyAdmins) {
            notificationService.notifyAdminsOfRegistration(tournament, displayName);
        }

        return Map.of("success", true);
    }

    public Map<String, Boolean> leaveTournament(String id, UserAccount currentUser) {
        TournamentParticipant participant = tournamentParticipantRepository.findByTournament_IdAndUserAccount_Id(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        participant.setStatus(ParticipantStatus.WITHDRAWN);
        tournamentParticipantRepository.save(participant);
        Tournament tournament = requireTournament(id);
        tournament.setParticipantCount((int) tournamentParticipantRepository.countByTournament_IdAndStatus(id, ParticipantStatus.REGISTERED));
        tournamentRepository.save(tournament);
        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<TournamentDtos.ParticipantDto> getParticipants(String tournamentId) {
        return tournamentParticipantRepository.findByTournament_IdOrderByIdAsc(tournamentId).stream()
                .map(participant -> apiMapper.toParticipantDto(
                        participant,
                        userProfileRepository.findById(participant.getUserAccount().getId()).map(UserProfile::getPhotoUrl).orElse(null)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TournamentDtos.MatchDto> getMatches(String tournamentId) {
        return matchRecordRepository.findByTournament_IdOrderByScheduledAtAsc(tournamentId).stream()
                .map(this::toMatchDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TournamentDtos.MatchDto getMatch(String id) {
        return toMatchDto(requireMatch(id));
    }

    public TournamentDtos.MatchDto createMatch(TournamentDtos.MatchUpsertRequest request) {
        MatchRecord matchRecord = new MatchRecord();
        matchRecord.setId("match" + System.currentTimeMillis());
        applyMatchUpdate(matchRecord, request);
        matchRecordRepository.save(matchRecord);
        syncMatchParticipants(matchRecord, request.participants());
        return toMatchDto(matchRecord);
    }

    public TournamentDtos.MatchDto updateMatch(String id, TournamentDtos.MatchUpsertRequest request) {
        MatchRecord matchRecord = requireMatch(id);
        applyMatchUpdate(matchRecord, request);
        matchRecordRepository.save(matchRecord);
        syncMatchParticipants(matchRecord, request.participants());
        return toMatchDto(matchRecord);
    }

    public Map<String, Boolean> submitScore(String matchId, TournamentDtos.ScoreSubmissionRequest request) {
        MatchRecord matchRecord = requireMatch(matchId);
        matchRecord.setWinnerUserId(request.winnerId());
        matchRecord.setScoreSummary(request.summary());
        matchRecord.setScoreJson(apiMapper.writeJson(request.fields()));
        matchRecord.setStatus(MatchStatus.COMPLETED);
        matchRecordRepository.save(matchRecord);
        return Map.of("success", true);
    }

    public TournamentDtos.MatchScoreDto getScore(String matchId) {
        return apiMapper.toMatchScoreDto(requireMatch(matchId));
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> getScoreAudit(String matchId) {
        MatchRecord matchRecord = requireMatch(matchId);
        if (matchRecord.getScoreJson() == null || matchRecord.getScoreJson().isBlank()) {
            return List.of();
        }
        return List.of(Map.of(
                "action", "SCORE_SUBMITTED",
                "summary", matchRecord.getScoreSummary() == null ? "" : matchRecord.getScoreSummary(),
                "at", apiMapper.formatDateTime(matchRecord.getScheduledAt())
        ));
    }

    public Map<String, Boolean> markAttendance(String matchId, TournamentDtos.AttendanceRequest request) {
        MatchRecord matchRecord = requireMatch(matchId);
        List<MatchParticipant> participants = matchParticipantRepository.findByMatchRecord_IdOrderByIdAsc(matchId);
        for (TournamentDtos.AttendanceRecordRequest record : request.records() == null ? List.<TournamentDtos.AttendanceRecordRequest>of() : request.records()) {
            participants.stream()
                    .filter(item -> item.getUserAccount().getId().equals(record.userId()))
                    .findFirst()
                    .ifPresent(item -> item.setAttendance(AttendanceStatus.valueOf(record.attendance())));
        }
        matchParticipantRepository.saveAll(participants);
        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<TournamentDtos.TeamDto> getTeams(String tournamentId) {
        return teamRepository.findByTournament_IdOrderByNameAsc(tournamentId).stream()
            .map(team -> apiMapper.toTeamDto(team, teamMemberRepository.findByTeam_IdOrderByIdAsc(team.getId()).stream().map(apiMapper::toTeamMemberDto).toList()))
                .toList();
    }

    public TournamentDtos.TeamDto createTeam(TournamentDtos.TeamCreateRequest request) {
        Team team = Team.builder()
                .id("team" + System.currentTimeMillis())
                .name(request.name())
                .tournament(requireTournament(request.tournamentId()))
                .build();
        teamRepository.save(team);
        return apiMapper.toTeamDto(team, List.of());
    }

    private Tournament requireTournament(String id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));
    }

    private MatchRecord requireMatch(String id) {
        return matchRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
    }

    private void applyTournamentUpdate(Tournament tournament, TournamentDtos.TournamentUpsertRequest request) {
        if (request.sportId() == null || request.sportId().isBlank()) {
            throw new BadRequestException("Sport is required");
        }
        tournament.setName(request.name());
        tournament.setSport(sportRepository.findById(request.sportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found")));
        tournament.setDescription(request.description());
        tournament.setRulesText(request.rules());
        tournament.setVenue(request.venue());
        tournament.setType(request.type() == null ? TournamentType.INDIVIDUAL : TournamentType.valueOf(request.type()));
        tournament.setStatus(request.status() == null
            ? (tournament.getStatus() == null ? TournamentStatus.DRAFT : tournament.getStatus())
            : TournamentStatus.valueOf(request.status()));
        tournament.setStartDate(parseDateTime(request.startDate()));
        tournament.setEndDate(parseDateTime(request.endDate()));
        tournament.setRegStartDate(parseDateTime(request.regStartDate()));
        tournament.setRegEndDate(parseDateTime(request.regEndDate()));
        tournament.setCapacity(request.capacity() == null ? 0 : request.capacity());
        tournament.setTeamMinSize(request.teamMinSize());
        tournament.setTeamMaxSize(request.teamMaxSize());
        tournament.setPointsWin(request.pointsWin() == null ? 3 : request.pointsWin());
        tournament.setPointsDraw(request.pointsDraw() == null ? 1 : request.pointsDraw());
        tournament.setPointsLoss(request.pointsLoss() == null ? 0 : request.pointsLoss());
        if (tournament.getParticipantCount() == 0) {
            tournament.setParticipantCount((int) tournamentParticipantRepository.countByTournament_IdAndStatus(tournament.getId(), ParticipantStatus.REGISTERED));
        }
    }

    private void applyMatchUpdate(MatchRecord matchRecord, TournamentDtos.MatchUpsertRequest request) {
        matchRecord.setTournament(requireTournament(request.tournamentId()));
        matchRecord.setRoundLabel(request.roundLabel());
        matchRecord.setScheduledAt(parseDateTime(request.scheduledAt()));
        matchRecord.setVenue(request.venue());
        matchRecord.setStatus(request.status() == null
            ? (matchRecord.getStatus() == null ? MatchStatus.SCHEDULED : matchRecord.getStatus())
            : MatchStatus.valueOf(request.status()));
    }

    private void syncMatchParticipants(MatchRecord matchRecord, List<TournamentDtos.MatchParticipantDto> participants) {
        List<MatchParticipant> existing = matchParticipantRepository.findByMatchRecord_IdOrderByIdAsc(matchRecord.getId());
        matchParticipantRepository.deleteAll(existing);
        if (participants == null) {
            return;
        }
        List<MatchParticipant> newParticipants = participants.stream().map(participant -> MatchParticipant.builder()
                .matchRecord(matchRecord)
                .userAccount(resolveUser(participant.userId()))
                .displayNameSnapshot(participant.displayName())
                .attendance(participant.attendance() == null ? null : AttendanceStatus.valueOf(participant.attendance()))
                .build()).toList();
        matchParticipantRepository.saveAll(newParticipants);
    }

    private UserAccount resolveUser(String userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TournamentDtos.MatchDto toMatchDto(MatchRecord matchRecord) {
        List<TournamentDtos.MatchParticipantDto> participants = matchParticipantRepository.findByMatchRecord_IdOrderByIdAsc(matchRecord.getId()).stream()
                .map(apiMapper::toMatchParticipantDto)
                .toList();
        return apiMapper.toMatchDto(matchRecord, participants);
    }

    private LocalDateTime parseDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        if (raw.endsWith("Z") || raw.contains("+") || raw.matches(".*-\\d\\d:\\d\\d$")) {
            return OffsetDateTime.parse(raw).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        }
        return LocalDateTime.parse(raw);
    }
}
