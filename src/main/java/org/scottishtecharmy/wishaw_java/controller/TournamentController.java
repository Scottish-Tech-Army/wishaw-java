package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.service.CurrentUserService;
import org.scottishtecharmy.wishaw_java.service.TournamentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({ApiPaths.V1 + "/tournaments", ApiPaths.LEGACY + "/tournaments"})
@Tag(name = "Tournaments", description = "Tournament browsing and management")
public class TournamentController {

    private final TournamentService tournamentService;
    private final CurrentUserService currentUserService;

    public TournamentController(TournamentService tournamentService, CurrentUserService currentUserService) {
        this.tournamentService = tournamentService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public TournamentDtos.TournamentListResponse getTournaments() {
        return tournamentService.getTournaments();
    }

    @GetMapping("/{id}")
    public TournamentDtos.TournamentDto getTournament(@PathVariable String id) {
        return tournamentService.getTournament(id);
    }

    @PostMapping
    public TournamentDtos.TournamentDto createTournament(@RequestBody TournamentDtos.TournamentUpsertRequest request) {
        return tournamentService.createTournament(request);
    }

    @PutMapping("/{id}")
    public TournamentDtos.TournamentDto updateTournament(@PathVariable String id, @RequestBody TournamentDtos.TournamentUpsertRequest request) {
        return tournamentService.updateTournament(id, request);
    }

    @PostMapping("/{id}/publish")
    public TournamentDtos.TournamentDto publish(@PathVariable String id) {
        return tournamentService.changeStatus(id, TournamentStatus.PUBLISHED);
    }

    @PostMapping("/{id}/cancel")
    public TournamentDtos.TournamentDto cancel(@PathVariable String id) {
        return tournamentService.changeStatus(id, TournamentStatus.CANCELLED);
    }

    @PostMapping("/{id}/complete")
    public TournamentDtos.TournamentDto complete(@PathVariable String id) {
        return tournamentService.changeStatus(id, TournamentStatus.COMPLETED);
    }

    @PostMapping("/{id}/join")
    public Map<String, Boolean> join(@PathVariable String id) {
        return tournamentService.joinTournament(id, currentUserService.requireCurrentUser());
    }

    @DeleteMapping("/{id}/leave")
    public Map<String, Boolean> leave(@PathVariable String id) {
        return tournamentService.leaveTournament(id, currentUserService.requireCurrentUser());
    }

    @GetMapping("/{id}/participants")
    public List<TournamentDtos.ParticipantDto> getParticipants(@PathVariable String id) {
        return tournamentService.getParticipants(id);
    }
}
