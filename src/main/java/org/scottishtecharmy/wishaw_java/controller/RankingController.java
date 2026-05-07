package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.RankingDTO;
import org.scottishtecharmy.wishaw_java.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rankings")
@RequiredArgsConstructor
@Tag(name = "Rankings", description = "Player ranking management APIs")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get leaderboard by tournament", description = "Returns rankings ordered by position")
    public ResponseEntity<ApiResponse<List<RankingDTO>>> getByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(rankingService.getRankingsByTournament(tournamentId)));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get rankings by player across tournaments")
    public ResponseEntity<ApiResponse<List<RankingDTO>>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(rankingService.getRankingsByPlayer(playerId)));
    }

    @GetMapping("/player/{playerId}/tournament/{tournamentId}")
    @Operation(summary = "Get player ranking in a specific tournament")
    public ResponseEntity<ApiResponse<RankingDTO>> getByPlayerAndTournament(
            @PathVariable Long playerId, @PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(
                rankingService.getRankingByPlayerAndTournament(playerId, tournamentId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update ranking", description = "Admin: Set or update a player's ranking")
    public ResponseEntity<ApiResponse<RankingDTO>> createOrUpdateRanking(@Valid @RequestBody RankingDTO dto) {
        RankingDTO result = rankingService.createOrUpdateRanking(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ranking saved successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete ranking")
    public ResponseEntity<ApiResponse<Void>> deleteRanking(@PathVariable Long id) {
        rankingService.deleteRanking(id);
        return ResponseEntity.ok(ApiResponse.success("Ranking deleted successfully", null));
    }
}

