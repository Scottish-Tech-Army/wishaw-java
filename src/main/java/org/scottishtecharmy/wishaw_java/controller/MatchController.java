package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.MatchDTO;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import org.scottishtecharmy.wishaw_java.service.MatchService;
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
@RequestMapping("/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "Match scheduling and management APIs")
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get matches by tournament")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getMatchesByTournament(tournamentId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get match by ID")
    public ResponseEntity<ApiResponse<MatchDTO>> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getMatchById(id)));
    }

    @GetMapping("/tournament/{tournamentId}/status/{status}")
    @Operation(summary = "Get matches by tournament and status")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getByTournamentAndStatus(
            @PathVariable Long tournamentId, @PathVariable MatchStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                matchService.getMatchesByTournamentAndStatus(tournamentId, status)));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get matches by player")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getMatchesByPlayer(playerId)));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get matches by team")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getMatchesByTeam(teamId)));
    }

    @GetMapping("/tournament/{tournamentId}/round/{roundNumber}")
    @Operation(summary = "Get matches by round")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getByRound(
            @PathVariable Long tournamentId, @PathVariable Integer roundNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                matchService.getMatchesByRound(tournamentId, roundNumber)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create match", description = "Admin: Schedule a new match")
    public ResponseEntity<ApiResponse<MatchDTO>> createMatch(@Valid @RequestBody MatchDTO dto) {
        MatchDTO created = matchService.createMatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Match created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update match", description = "Admin: Update match details, status, or winner")
    public ResponseEntity<ApiResponse<MatchDTO>> updateMatch(
            @PathVariable Long id, @RequestBody MatchDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Match updated successfully",
                matchService.updateMatch(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete match")
    public ResponseEntity<ApiResponse<Void>> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.ok(ApiResponse.success("Match deleted successfully", null));
    }
}

