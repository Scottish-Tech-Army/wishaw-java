package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.TeamDTO;
import org.scottishtecharmy.wishaw_java.service.TeamService;
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
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Team creation and member management APIs")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get teams by tournament")
    public ResponseEntity<ApiResponse<List<TeamDTO>>> getTeamsByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getTeamsByTournament(tournamentId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<ApiResponse<TeamDTO>> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getTeamById(id)));
    }

    @GetMapping("/captain/{captainId}")
    @Operation(summary = "Get teams by captain")
    public ResponseEntity<ApiResponse<List<TeamDTO>>> getTeamsByCaptain(@PathVariable Long captainId) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getTeamsByCaptain(captainId)));
    }

    @PostMapping
    @Operation(summary = "Create team", description = "Create a new team for a tournament")
    public ResponseEntity<ApiResponse<TeamDTO>> createTeam(@Valid @RequestBody TeamDTO dto) {
        TeamDTO created = teamService.createTeam(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Team created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team")
    public ResponseEntity<ApiResponse<TeamDTO>> updateTeam(@PathVariable Long id, @RequestBody TeamDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Team updated successfully",
                teamService.updateTeam(id, dto)));
    }

    @PostMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Add member to team")
    public ResponseEntity<ApiResponse<TeamDTO>> addMember(
            @PathVariable Long teamId, @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Member added successfully",
                teamService.addMember(teamId, userId)));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Remove member from team")
    public ResponseEntity<ApiResponse<TeamDTO>> removeMember(
            @PathVariable Long teamId, @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully",
                teamService.removeMember(teamId, userId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete team", description = "Admin: Delete a team")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.success("Team deleted successfully", null));
    }
}

