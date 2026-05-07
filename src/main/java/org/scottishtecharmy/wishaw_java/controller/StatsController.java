package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.StatsDtos;
import org.scottishtecharmy.wishaw_java.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ApiPaths.V1 + "/stats", ApiPaths.LEGACY + "/stats"})
@Tag(name = "Stats", description = "Player and admin analytics endpoints")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/player/{userId}")
    public StatsDtos.PlayerStatsDto getPlayerStats(@PathVariable String userId) {
        return statsService.getPlayerStats(userId);
    }

    @GetMapping("/admin/dashboard")
    public StatsDtos.AdminDashboardDto getAdminDashboard() {
        return statsService.getAdminDashboard();
    }
}
