package org.scottishtecharmy.wishaw_java.leaderboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.leaderboard.dto.LeaderboardEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Global and centre-based leaderboards")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Get the global leaderboard (all centres)")
    public ResponseEntity<List<LeaderboardEntry>> getGlobal() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/centre/{centreId}")
    @Operation(summary = "Get leaderboard for a specific centre")
    public ResponseEntity<List<LeaderboardEntry>> getByCentre(@PathVariable Long centreId) {
        return ResponseEntity.ok(leaderboardService.getCentreLeaderboard(centreId));
    }
}

