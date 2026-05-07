package org.scottishtecharmy.wishaw_java.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.group.dto.GameGroupRequest;
import org.scottishtecharmy.wishaw_java.group.dto.GameGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Game Groups", description = "Manage game groups per centre (e.g. Minecraft group at Wishaw)")
public class GameGroupController {

    private final GameGroupService gameGroupService;

    @GetMapping
    @Operation(summary = "List all game groups")
    public ResponseEntity<List<GameGroupResponse>> getAll() {
        return ResponseEntity.ok(gameGroupService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game group by ID")
    public ResponseEntity<GameGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gameGroupService.findById(id));
    }

    @GetMapping("/centre/{centreId}")
    @Operation(summary = "List game groups for a centre")
    public ResponseEntity<List<GameGroupResponse>> getByCentre(@PathVariable Long centreId) {
        return ResponseEntity.ok(gameGroupService.findByCentre(centreId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Create a game group (admin only)")
    public ResponseEntity<GameGroupResponse> create(@Valid @RequestBody GameGroupRequest request) {
        return ResponseEntity.ok(gameGroupService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Update a game group (admin only)")
    public ResponseEntity<GameGroupResponse> update(@PathVariable Long id, @Valid @RequestBody GameGroupRequest request) {
        return ResponseEntity.ok(gameGroupService.update(id, request));
    }

    @PostMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Add a user to a game group (admin only)")
    public ResponseEntity<GameGroupResponse> addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return ResponseEntity.ok(gameGroupService.addMember(groupId, userId));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Remove a user from a game group (admin only)")
    public ResponseEntity<GameGroupResponse> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return ResponseEntity.ok(gameGroupService.removeMember(groupId, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Delete a game group (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

