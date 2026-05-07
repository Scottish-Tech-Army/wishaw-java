package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.service.TournamentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({ApiPaths.V1 + "/teams", ApiPaths.LEGACY + "/teams"})
@Tag(name = "Teams", description = "Tournament team endpoints")
public class TeamController {

    private final TournamentService tournamentService;

    public TeamController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/tournament/{id}")
    public List<TournamentDtos.TeamDto> getTeams(@PathVariable String id) {
        return tournamentService.getTeams(id);
    }

    @PostMapping
    public TournamentDtos.TeamDto createTeam(@RequestBody TournamentDtos.TeamCreateRequest request) {
        return tournamentService.createTeam(request);
    }
}
