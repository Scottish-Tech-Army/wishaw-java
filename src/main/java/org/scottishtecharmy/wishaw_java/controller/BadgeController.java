package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.BadgeDTO;
import org.scottishtecharmy.wishaw_java.dto.PlayerBadgeDTO;
import org.scottishtecharmy.wishaw_java.service.BadgeService;
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
@RequestMapping("/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Badge definition and player badge assignment APIs")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "Get all badges")
    public ResponseEntity<ApiResponse<List<BadgeDTO>>> getAllBadges() {
        return ResponseEntity.ok(ApiResponse.success(badgeService.getAllBadges()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get badge by ID")
    public ResponseEntity<ApiResponse<BadgeDTO>> getBadgeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(badgeService.getBadgeById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create badge", description = "Admin: Create a new badge definition")
    public ResponseEntity<ApiResponse<BadgeDTO>> createBadge(@Valid @RequestBody BadgeDTO dto) {
        BadgeDTO created = badgeService.createBadge(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Badge created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update badge")
    public ResponseEntity<ApiResponse<BadgeDTO>> updateBadge(
            @PathVariable Long id, @RequestBody BadgeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Badge updated successfully",
                badgeService.updateBadge(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete badge")
    public ResponseEntity<ApiResponse<Void>> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.ok(ApiResponse.success("Badge deleted successfully", null));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get badges awarded to a player")
    public ResponseEntity<ApiResponse<List<PlayerBadgeDTO>>> getPlayerBadges(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(badgeService.getBadgesByPlayer(playerId)));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get badges awarded in a tournament")
    public ResponseEntity<ApiResponse<List<PlayerBadgeDTO>>> getTournamentBadges(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(badgeService.getBadgesByTournament(tournamentId)));
    }

    @PostMapping("/award")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Award badge to player", description = "Admin: Award a badge to a player for a tournament")
    public ResponseEntity<ApiResponse<PlayerBadgeDTO>> awardBadge(
            @Valid @RequestBody PlayerBadgeDTO dto, @RequestParam Long adminId) {
        PlayerBadgeDTO awarded = badgeService.awardBadge(dto, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Badge awarded successfully", awarded));
    }

    @DeleteMapping("/revoke/{playerBadgeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revoke badge from player")
    public ResponseEntity<ApiResponse<Void>> revokeBadge(@PathVariable Long playerBadgeId) {
        badgeService.revokeBadge(playerBadgeId);
        return ResponseEntity.ok(ApiResponse.success("Badge revoked successfully", null));
    }
}

