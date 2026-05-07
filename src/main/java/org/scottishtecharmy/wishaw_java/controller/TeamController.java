package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /** GET /api/v1/teams */
    @GetMapping
    public ResponseEntity<List<TeamSummaryDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    /** GET /api/v1/teams/{teamId} */
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailDto> getTeamDetail(@PathVariable String teamId) {
        return ResponseEntity.ok(teamService.getTeamDetail(teamId));
    }
}
