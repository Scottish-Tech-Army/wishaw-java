package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.response.LeaderboardEntryResponse;
import org.scottishtecharmy.wishaw_java.service.leaderboard.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboards")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    // FRONTEND_INTEGRATION: Leaderboard cards and tables can consume these responses directly.
    @GetMapping("/global")
    public List<LeaderboardEntryResponse> getGlobalLeaderboard() {
        return leaderboardService.getGlobalLeaderboard();
    }

    @GetMapping("/centre/{centreId}")
    public List<LeaderboardEntryResponse> getCentreLeaderboard(@PathVariable Long centreId) {
        return leaderboardService.getCentreLeaderboard(centreId);
    }

    @GetMapping("/group/{groupId}")
    public List<LeaderboardEntryResponse> getGroupLeaderboard(@PathVariable Long groupId) {
        return leaderboardService.getGroupLeaderboard(groupId);
    }
}