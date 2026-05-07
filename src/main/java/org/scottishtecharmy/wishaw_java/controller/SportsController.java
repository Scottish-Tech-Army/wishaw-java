package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.service.SportService;
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
@RequestMapping({ApiPaths.V1 + "/sports", ApiPaths.LEGACY + "/sports"})
@Tag(name = "Sports", description = "Sport catalogue management")
public class SportsController {

    private final SportService sportService;

    public SportsController(SportService sportService) {
        this.sportService = sportService;
    }

    @GetMapping
    public List<TournamentDtos.SportDto> getSports() {
        return sportService.getSports();
    }

    @PostMapping
    public TournamentDtos.SportDto createSport(@RequestBody TournamentDtos.SportUpsertRequest request) {
        return sportService.createSport(request);
    }

    @PutMapping("/{id}")
    public TournamentDtos.SportDto updateSport(@PathVariable String id, @RequestBody TournamentDtos.SportUpsertRequest request) {
        return sportService.updateSport(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteSport(@PathVariable String id) {
        return sportService.deleteSport(id);
    }
}
