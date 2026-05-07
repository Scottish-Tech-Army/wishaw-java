package org.scottishtecharmy.wishaw_java.badge;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.badge.dto.BadgeRequest;
import org.scottishtecharmy.wishaw_java.badge.dto.BadgeResponse;
import org.scottishtecharmy.wishaw_java.badge.dto.SubBadgeRequest;
import org.scottishtecharmy.wishaw_java.badge.dto.SubBadgeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Core badges and sub-badge/challenge management")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "List all core badges")
    public ResponseEntity<List<BadgeResponse>> getAllBadges() {
        return ResponseEntity.ok(badgeService.findAllBadges());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a badge by ID")
    public ResponseEntity<BadgeResponse> getBadge(@PathVariable Long id) {
        return ResponseEntity.ok(badgeService.findBadgeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Create a new badge (MAIN_ADMIN only)")
    public ResponseEntity<BadgeResponse> createBadge(@Valid @RequestBody BadgeRequest request) {
        return ResponseEntity.ok(badgeService.createBadge(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Update a badge (MAIN_ADMIN only)")
    public ResponseEntity<BadgeResponse> updateBadge(@PathVariable Long id, @Valid @RequestBody BadgeRequest request) {
        return ResponseEntity.ok(badgeService.updateBadge(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Delete a badge (MAIN_ADMIN only)")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }

    // --- Sub-badges / Challenges ---

    @GetMapping("/{badgeId}/sub-badges")
    @Operation(summary = "List sub-badges for a given core badge")
    public ResponseEntity<List<SubBadgeResponse>> getSubBadgesByBadge(@PathVariable Long badgeId) {
        return ResponseEntity.ok(badgeService.findSubBadgesByBadge(badgeId));
    }

    @GetMapping("/sub-badges/module/{moduleId}")
    @Operation(summary = "List sub-badges for a given module")
    public ResponseEntity<List<SubBadgeResponse>> getSubBadgesByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(badgeService.findSubBadgesByModule(moduleId));
    }

    @GetMapping("/sub-badges/{id}")
    @Operation(summary = "Get a sub-badge by ID")
    public ResponseEntity<SubBadgeResponse> getSubBadge(@PathVariable Long id) {
        return ResponseEntity.ok(badgeService.findSubBadgeById(id));
    }

    @PostMapping("/sub-badges")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Create a sub-badge/challenge (admin only)")
    public ResponseEntity<SubBadgeResponse> createSubBadge(@Valid @RequestBody SubBadgeRequest request) {
        return ResponseEntity.ok(badgeService.createSubBadge(request));
    }

    @PutMapping("/sub-badges/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Update a sub-badge/challenge (admin only)")
    public ResponseEntity<SubBadgeResponse> updateSubBadge(@PathVariable Long id, @Valid @RequestBody SubBadgeRequest request) {
        return ResponseEntity.ok(badgeService.updateSubBadge(id, request));
    }

    @DeleteMapping("/sub-badges/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Delete a sub-badge/challenge (admin only)")
    public ResponseEntity<Void> deleteSubBadge(@PathVariable Long id) {
        badgeService.deleteSubBadge(id);
        return ResponseEntity.noContent().build();
    }
}
