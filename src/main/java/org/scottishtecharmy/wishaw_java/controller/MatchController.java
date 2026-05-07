package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.service.TournamentService;
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
@RequestMapping({ApiPaths.V1 + "/matches", ApiPaths.LEGACY + "/matches"})
@Tag(name = "Matches", description = "Match scheduling, scoring, and attendance")
public class MatchController {

    private final TournamentService tournamentService;

    public MatchController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/tournament/{id}")
    public List<TournamentDtos.MatchDto> getMatches(@PathVariable String id) {
        return tournamentService.getMatches(id);
    }

    @GetMapping("/{id}")
    public TournamentDtos.MatchDto getMatch(@PathVariable String id) {
        return tournamentService.getMatch(id);
    }

    @PostMapping
    public TournamentDtos.MatchDto createMatch(@RequestBody TournamentDtos.MatchUpsertRequest request) {
        return tournamentService.createMatch(request);
    }

    @PutMapping("/{id}")
    public TournamentDtos.MatchDto updateMatch(@PathVariable String id, @RequestBody TournamentDtos.MatchUpsertRequest request) {
        return tournamentService.updateMatch(id, request);
    }

    @PostMapping("/{id}/score")
    public Map<String, Boolean> submitScore(@PathVariable String id, @RequestBody TournamentDtos.ScoreSubmissionRequest request) {
        return tournamentService.submitScore(id, request);
    }

    @GetMapping("/{id}/score")
    public TournamentDtos.MatchScoreDto getScore(@PathVariable String id) {
        return tournamentService.getScore(id);
    }

    @GetMapping("/{id}/score/audit")
    public List<Map<String, String>> getScoreAudit(@PathVariable String id) {
        return tournamentService.getScoreAudit(id);
    }

    @PostMapping("/{id}/attendance")
    public Map<String, Boolean> markAttendance(@PathVariable String id, @RequestBody TournamentDtos.AttendanceRequest request) {
        return tournamentService.markAttendance(id, request);
    }
}
