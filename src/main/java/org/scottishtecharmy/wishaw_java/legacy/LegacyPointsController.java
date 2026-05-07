package org.scottishtecharmy.wishaw_java.legacy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.legacy.dto.LegacyPointsRequest;
import org.scottishtecharmy.wishaw_java.legacy.dto.LegacyPointsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legacy-points")
@RequiredArgsConstructor
@Tag(name = "Legacy Points", description = "Manage initial/legacy points for users per badge (no sub-badge breakdown)")
public class LegacyPointsController {

    private final LegacyPointsService legacyPointsService;

    @GetMapping
    @Operation(summary = "Get all legacy points entries")
    public ResponseEntity<List<LegacyPointsResponse>> getAll() {
        return ResponseEntity.ok(legacyPointsService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a legacy points entry by ID")
    public ResponseEntity<LegacyPointsResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(legacyPointsService.getById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all legacy points for a specific user")
    public ResponseEntity<List<LegacyPointsResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(legacyPointsService.getByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Create a legacy points entry for a user and badge (admin only)")
    public ResponseEntity<LegacyPointsResponse> create(@RequestBody LegacyPointsRequest request) {
        return ResponseEntity.ok(legacyPointsService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Update a legacy points entry (admin only)")
    public ResponseEntity<LegacyPointsResponse> update(@PathVariable Long id, @RequestBody LegacyPointsRequest request) {
        return ResponseEntity.ok(legacyPointsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Delete a legacy points entry (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        legacyPointsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

