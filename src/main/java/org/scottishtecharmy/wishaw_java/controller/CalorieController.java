package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.CalorieRecordDTO;
import org.scottishtecharmy.wishaw_java.service.CalorieService;
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
@RequestMapping("/calories")
@RequiredArgsConstructor
@Tag(name = "Calories", description = "Calorie tracking and entry APIs")
public class CalorieController {

    private final CalorieService calorieService;

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get calorie records by player")
    public ResponseEntity<ApiResponse<List<CalorieRecordDTO>>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(calorieService.getCaloriesByPlayer(playerId)));
    }

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get calorie records by match")
    public ResponseEntity<ApiResponse<List<CalorieRecordDTO>>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.success(calorieService.getCaloriesByMatch(matchId)));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get calorie records by tournament")
    public ResponseEntity<ApiResponse<List<CalorieRecordDTO>>> getByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(calorieService.getCaloriesByTournament(tournamentId)));
    }

    @GetMapping("/player/{playerId}/total")
    @Operation(summary = "Get total calories burned by player")
    public ResponseEntity<ApiResponse<Double>> getTotalByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(calorieService.getTotalCaloriesByPlayer(playerId)));
    }

    @GetMapping("/player/{playerId}/tournament/{tournamentId}/total")
    @Operation(summary = "Get total calories burned by player in a tournament")
    public ResponseEntity<ApiResponse<Double>> getTotalByPlayerAndTournament(
            @PathVariable Long playerId, @PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(
                calorieService.getTotalCaloriesByPlayerAndTournament(playerId, tournamentId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add calorie record", description = "Admin: Manually enter calories burned for a player")
    public ResponseEntity<ApiResponse<CalorieRecordDTO>> addCalorieRecord(
            @Valid @RequestBody CalorieRecordDTO dto, @RequestParam Long adminId) {
        CalorieRecordDTO result = calorieService.addCalorieRecord(dto, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Calorie record added successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete calorie record")
    public ResponseEntity<ApiResponse<Void>> deleteCalorieRecord(@PathVariable Long id) {
        calorieService.deleteCalorieRecord(id);
        return ResponseEntity.ok(ApiResponse.success("Calorie record deleted successfully", null));
    }
}

