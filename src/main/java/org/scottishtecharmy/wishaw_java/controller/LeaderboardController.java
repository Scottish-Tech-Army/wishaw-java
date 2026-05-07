package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.LeaderboardDtos;
import org.scottishtecharmy.wishaw_java.service.CurrentUserService;
import org.scottishtecharmy.wishaw_java.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({ApiPaths.V1 + "/leaderboard", ApiPaths.LEGACY + "/leaderboard"})
@Tag(name = "Leaderboard", description = "Leaderboards, earned badges, and calorie tracking")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final CurrentUserService currentUserService;

    public LeaderboardController(LeaderboardService leaderboardService, CurrentUserService currentUserService) {
        this.leaderboardService = leaderboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/tournament/{id}")
    public List<LeaderboardDtos.LeaderboardEntryDto> getTournamentLeaderboard(@PathVariable String id) {
        return leaderboardService.getTournamentLeaderboard(id);
    }

    @GetMapping("/global")
    public List<LeaderboardDtos.LeaderboardEntryDto> getGlobalLeaderboard() {
        return leaderboardService.getGlobalLeaderboard();
    }

    @GetMapping("/badges")
    public List<LeaderboardDtos.LtcBadgeDto> getBadges() {
        return leaderboardService.getBadges();
    }

    @PostMapping("/badges")
    public LeaderboardDtos.LtcBadgeDto createBadge(@RequestBody LeaderboardDtos.CreateBadgeRequest request) {
        return leaderboardService.createBadge(request);
    }

    @PostMapping("/badges/assign")
    public Map<String, Boolean> assignBadge(@RequestBody LeaderboardDtos.BadgeAssignRequest request) {
        return leaderboardService.assignBadge(request);
    }

    @GetMapping("/badges/user/{userId}")
    public List<LeaderboardDtos.EarnedBadgeDto> getUserBadges(@PathVariable String userId) {
        return leaderboardService.getUserBadges(userId);
    }

    @PostMapping("/calories")
    public Map<String, Boolean> logCalories(@RequestBody LeaderboardDtos.CaloriesLogRequest request) {
        String userId = request.userId() == null || request.userId().isBlank()
                ? currentUserService.requireCurrentUser().getId()
                : request.userId();
        return leaderboardService.logCalories(new LeaderboardDtos.CaloriesLogRequest(userId, request.sportName(), request.calories()));
    }

    @GetMapping("/calories/user/{userId}")
    public LeaderboardDtos.CaloriesSummaryDto getUserCalories(@PathVariable String userId) {
        return leaderboardService.getCalories(userId);
    }
}
