package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.LeaderboardResponseDto;
import org.scottishtecharmy.wishaw_java.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * GET /api/v1/leaderboard?period=ALL_TIME&sortBy=XP&page=0&size=50
     *
     * The current user's username is extracted from the JWT (via SecurityContext)
     * so the frontend can highlight the logged-in student's row.
     */
    @GetMapping
    public ResponseEntity<LeaderboardResponseDto> getLeaderboard(
            @RequestParam(defaultValue = "ALL_TIME") String period,
            @RequestParam(defaultValue = "XP") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {

        String currentUsername = authentication != null ? authentication.getName() : null;

        return ResponseEntity.ok(
                leaderboardService.getLeaderboard(period, sortBy, page, size, currentUsername));
    }
}
