package org.scottishtecharmy.wishaw_java.progress;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.progress.dto.AwardPointsRequest;
import org.scottishtecharmy.wishaw_java.progress.dto.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.progress.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "XP tracking, sub-badge completion, and user profiles")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get full user profile with all badge progress, XP, and level")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(progressService.getUserProfile(userId));
    }

    @GetMapping("/{userId}/badge/{badgeId}")
    @Operation(summary = "Get progress for a single badge for a user")
    public ResponseEntity<BadgeProgressResponse> getBadgeProgress(
            @PathVariable Long userId, @PathVariable Long badgeId) {
        return ResponseEntity.ok(progressService.getBadgeProgress(userId, badgeId));
    }

    @GetMapping("/{userId}/completed-sub-badges")
    @Operation(summary = "Get IDs of all sub-badges completed by a user")
    public ResponseEntity<Set<Long>> getCompletedSubBadgeIds(@PathVariable Long userId) {
        return ResponseEntity.ok(progressService.getCompletedSubBadgeIds(userId));
    }

    @PostMapping("/complete")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Mark a sub-badge as completed for a user and award XP (admin only)")
    public ResponseEntity<BadgeProgressResponse> completeSubBadge(@RequestBody AwardPointsRequest request) {
        return ResponseEntity.ok(progressService.completeSubBadge(request.userId(), request.subBadgeId()));
    }
}
