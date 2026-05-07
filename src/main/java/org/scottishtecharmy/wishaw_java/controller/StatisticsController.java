package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.AdminDashboardDTO;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.PlayerStatsDTO;
import org.scottishtecharmy.wishaw_java.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics & Dashboards", description = "Player statistics and admin analytics APIs")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get player statistics", description = "Player: View personal performance dashboard including wins, attendance, badges, calories")
    public ResponseEntity<ApiResponse<PlayerStatsDTO>> getPlayerStats(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(statisticsService.getPlayerStats(playerId)));
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin dashboard", description = "Admin: View analytics across all tournaments")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(statisticsService.getAdminDashboard()));
    }
}

