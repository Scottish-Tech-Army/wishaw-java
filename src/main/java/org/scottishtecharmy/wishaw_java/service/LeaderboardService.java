package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.LeaderboardDtos;
import org.scottishtecharmy.wishaw_java.entity.CaloriesLog;
import org.scottishtecharmy.wishaw_java.entity.MainBadge;
import org.scottishtecharmy.wishaw_java.entity.TournamentParticipant;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.entity.UserSubBadge;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.CaloriesLogRepository;
import org.scottishtecharmy.wishaw_java.repository.MainBadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.UserProfileRepository;
import org.scottishtecharmy.wishaw_java.repository.UserSubBadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaderboardService {

    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSubBadgeRepository userSubBadgeRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final CaloriesLogRepository caloriesLogRepository;
    private final BadgeLevelService badgeLevelService;
    private final ApiMapper apiMapper;

    public LeaderboardService(TournamentParticipantRepository tournamentParticipantRepository,
                              UserProfileRepository userProfileRepository,
                              UserSubBadgeRepository userSubBadgeRepository,
                              MainBadgeRepository mainBadgeRepository,
                              CaloriesLogRepository caloriesLogRepository,
                              BadgeLevelService badgeLevelService,
                              ApiMapper apiMapper) {
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.userProfileRepository = userProfileRepository;
        this.userSubBadgeRepository = userSubBadgeRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.caloriesLogRepository = caloriesLogRepository;
        this.badgeLevelService = badgeLevelService;
        this.apiMapper = apiMapper;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDtos.LeaderboardEntryDto> getTournamentLeaderboard(String tournamentId) {
        List<TournamentParticipant> participants = tournamentParticipantRepository.findByTournament_IdOrderByIdAsc(tournamentId);
        return participants.stream()
                .map(TournamentParticipant::getUserAccount)
                .distinct()
                .map(this::toLeaderboardEntry)
                .sorted((left, right) -> Integer.compare(right.totalPoints(), left.totalPoints()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDtos.LeaderboardEntryDto> getGlobalLeaderboard() {
        return userProfileRepository.findAll().stream()
                .map(UserProfile::getUserAccount)
                .map(this::toLeaderboardEntry)
                .sorted((left, right) -> Integer.compare(right.totalPoints(), left.totalPoints()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDtos.LtcBadgeDto> getBadges() {
        return mainBadgeRepository.findAll().stream()
                .map(badge -> new LeaderboardDtos.LtcBadgeDto(badge.getId(), badge.getName(), badge.getIcon(), badge.getDescription()))
                .toList();
    }

    public LeaderboardDtos.LtcBadgeDto createBadge(LeaderboardDtos.CreateBadgeRequest request) {
        MainBadge badge = MainBadge.builder()
                .id("mb-" + UUID.randomUUID())
                .name(request.name())
                .icon(request.icon())
                .description(request.description())
                .build();
        mainBadgeRepository.save(badge);
        return new LeaderboardDtos.LtcBadgeDto(badge.getId(), badge.getName(), badge.getIcon(), badge.getDescription());
    }

    public Map<String, Boolean> assignBadge(LeaderboardDtos.BadgeAssignRequest request) {
        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDtos.EarnedBadgeDto> getUserBadges(String userId) {
        return userSubBadgeRepository.findByUserAccount_Id(userId).stream()
                .map(award -> new LeaderboardDtos.EarnedBadgeDto(
                        award.getSubBadge().getMainBadge().getId(),
                        award.getSubBadge().getMainBadge().getName(),
                        award.getSubBadge().getMainBadge().getIcon(),
                        award.getAwardedAt().toString()
                ))
                .distinct()
                .toList();
    }

    public Map<String, Boolean> logCalories(LeaderboardDtos.CaloriesLogRequest request) {
        caloriesLogRepository.save(CaloriesLog.builder()
                .userAccount(userProfileRepository.findById(request.userId()).orElseThrow().getUserAccount())
                .sportName(request.sportName())
                .calories(request.calories() == null ? 0 : request.calories())
                .loggedAt(Instant.now())
                .build());
        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public LeaderboardDtos.CaloriesSummaryDto getCalories(String userId) {
        List<CaloriesLog> logs = caloriesLogRepository.findByUserAccount_IdOrderByLoggedAtDesc(userId);
        Map<String, Integer> bySport = new LinkedHashMap<>();
        int total = 0;
        for (CaloriesLog log : logs) {
            bySport.merge(log.getSportName(), log.getCalories(), Integer::sum);
            total += log.getCalories();
        }
        return new LeaderboardDtos.CaloriesSummaryDto(total, bySport);
    }

    private LeaderboardDtos.LeaderboardEntryDto toLeaderboardEntry(org.scottishtecharmy.wishaw_java.entity.UserAccount userAccount) {
        UserProfile profile = userProfileRepository.findById(userAccount.getId()).orElseThrow();
        List<UserSubBadge> awards = userSubBadgeRepository.findByUserAccount_Id(userAccount.getId());
        int totalPoints = awards.stream().mapToInt(item -> item.getSubBadge().getPoints()).sum();
        Map<String, String> badgeLevels = mainBadgeRepository.findAll().stream().collect(Collectors.toMap(
                MainBadge::getName,
                badge -> {
                    int points = awards.stream()
                            .filter(item -> item.getSubBadge().getMainBadge().getId().equals(badge.getId()))
                            .mapToInt(item -> item.getSubBadge().getPoints())
                            .sum();
                    return badgeLevelService.resolveLabel(points);
                },
                (left, right) -> left,
                LinkedHashMap::new
        ));
        long completedModules = awards.stream().map(item -> item.getSubBadge().getLearningModule().getId()).distinct().count();
        return new LeaderboardDtos.LeaderboardEntryDto(
                userAccount.getId(),
                profile.getDisplayName(),
                userAccount.getCentre() == null ? null : userAccount.getCentre().getId(),
                userAccount.getCentre() == null ? null : userAccount.getCentre().getName(),
                totalPoints,
                badgeLevels,
                (int) completedModules
        );
    }
}
