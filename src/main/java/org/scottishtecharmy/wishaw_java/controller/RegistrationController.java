package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.RegistrationDTO;
import org.scottishtecharmy.wishaw_java.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
@Tag(name = "Registrations", description = "Tournament registration and participation APIs")
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get registrations by tournament")
    public ResponseEntity<ApiResponse<List<RegistrationDTO>>> getByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getRegistrationsByTournament(tournamentId)));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get registrations by player")
    public ResponseEntity<ApiResponse<List<RegistrationDTO>>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getRegistrationsByPlayer(playerId)));
    }

    @PostMapping
    @Operation(summary = "Register player for tournament", description = "Direct join - no approval needed")
    public ResponseEntity<ApiResponse<RegistrationDTO>> register(@Valid @RequestBody RegistrationDTO dto) {
        RegistrationDTO created = registrationService.registerPlayer(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Player registered successfully", created));
    }

    @DeleteMapping("/tournament/{tournamentId}/player/{playerId}")
    @Operation(summary = "Unregister player from tournament")
    public ResponseEntity<ApiResponse<Void>> unregister(
            @PathVariable Long tournamentId, @PathVariable Long playerId) {
        registrationService.unregisterPlayer(tournamentId, playerId);
        return ResponseEntity.ok(ApiResponse.success("Player unregistered successfully", null));
    }
}

