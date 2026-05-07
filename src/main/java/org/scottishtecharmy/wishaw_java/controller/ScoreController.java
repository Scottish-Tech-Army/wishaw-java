package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.ScoreAuditLogDTO;
import org.scottishtecharmy.wishaw_java.dto.ScoreDTO;
import org.scottishtecharmy.wishaw_java.service.ScoreService;
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
@RequestMapping("/scores")
@RequiredArgsConstructor
@Tag(name = "Scores", description = "Score management APIs with audit trail")
public class ScoreController {

    private final ScoreService scoreService;

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get scores by match")
    public ResponseEntity<ApiResponse<List<ScoreDTO>>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.success(scoreService.getScoresByMatch(matchId)));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get scores by player")
    public ResponseEntity<ApiResponse<List<ScoreDTO>>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(scoreService.getScoresByPlayer(playerId)));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get scores by team")
    public ResponseEntity<ApiResponse<List<ScoreDTO>>> getByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.success(scoreService.getScoresByTeam(teamId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update score", description = "Admin: Enter or update a score. Generates audit log on update.")
    public ResponseEntity<ApiResponse<ScoreDTO>> createOrUpdateScore(
            @Valid @RequestBody ScoreDTO dto, @RequestParam Long adminId) {
        ScoreDTO result = scoreService.createOrUpdateScore(dto, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Score saved successfully", result));
    }

    @GetMapping("/audit/score/{scoreId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by score ID", description = "Admin: View score change history")
    public ResponseEntity<ApiResponse<List<ScoreAuditLogDTO>>> getAuditByScore(@PathVariable Long scoreId) {
        return ResponseEntity.ok(ApiResponse.success(scoreService.getAuditLogsByScore(scoreId)));
    }

    @GetMapping("/audit/match/{matchId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by match ID", description = "Admin: View all score changes for a match")
    public ResponseEntity<ApiResponse<List<ScoreAuditLogDTO>>> getAuditByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.success(scoreService.getAuditLogsByMatch(matchId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete score")
    public ResponseEntity<ApiResponse<Void>> deleteScore(@PathVariable Long id) {
        scoreService.deleteScore(id);
        return ResponseEntity.ok(ApiResponse.success("Score deleted successfully", null));
    }
}

