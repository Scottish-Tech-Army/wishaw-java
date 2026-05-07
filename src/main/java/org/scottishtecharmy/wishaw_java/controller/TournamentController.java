package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.TournamentDTO;
import org.scottishtecharmy.wishaw_java.enums.SportType;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.service.TournamentService;
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
@RequestMapping("/tournaments")
@RequiredArgsConstructor
@Tag(name = "Tournaments", description = "Tournament creation and management APIs")
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    @Operation(summary = "Get all tournaments")
    public ResponseEntity<ApiResponse<List<TournamentDTO>>> getAllTournaments() {
        return ResponseEntity.ok(ApiResponse.success(tournamentService.getAllTournaments()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tournament by ID")
    public ResponseEntity<ApiResponse<TournamentDTO>> getTournamentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tournamentService.getTournamentById(id)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tournaments by status")
    public ResponseEntity<ApiResponse<List<TournamentDTO>>> getTournamentsByStatus(@PathVariable TournamentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(tournamentService.getTournamentsByStatus(status)));
    }

    @GetMapping("/sport/{sportType}")
    @Operation(summary = "Get tournaments by sport type")
    public ResponseEntity<ApiResponse<List<TournamentDTO>>> getTournamentsBySport(@PathVariable SportType sportType) {
        return ResponseEntity.ok(ApiResponse.success(tournamentService.getTournamentsBySport(sportType)));
    }

    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get tournaments created by admin")
    public ResponseEntity<ApiResponse<List<TournamentDTO>>> getTournamentsByAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(ApiResponse.success(tournamentService.getTournamentsByAdmin(adminId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create tournament", description = "Admin: Create a new tournament")
    public ResponseEntity<ApiResponse<TournamentDTO>> createTournament(
            @Valid @RequestBody TournamentDTO dto, @RequestParam Long adminId) {
        TournamentDTO created = tournamentService.createTournament(dto, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tournament created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update tournament")
    public ResponseEntity<ApiResponse<TournamentDTO>> updateTournament(
            @PathVariable Long id, @RequestBody TournamentDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Tournament updated successfully",
                tournamentService.updateTournament(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete tournament")
    public ResponseEntity<ApiResponse<Void>> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.ok(ApiResponse.success("Tournament deleted successfully", null));
    }
}

