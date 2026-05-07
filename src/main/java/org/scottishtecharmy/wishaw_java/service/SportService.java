package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.entity.Sport;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.SportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class SportService {

    private static final String DEFAULT_SPORT_ICON = "\uD83C\uDFCF";

    private final SportRepository sportRepository;
    private final ApiMapper apiMapper;
    private final AgePolicyService agePolicyService;

    public SportService(SportRepository sportRepository, ApiMapper apiMapper, AgePolicyService agePolicyService) {
        this.sportRepository = sportRepository;
        this.apiMapper = apiMapper;
        this.agePolicyService = agePolicyService;
    }

    @Transactional(readOnly = true)
    public List<TournamentDtos.SportDto> getSports() {
        return sportRepository.findAll().stream().map(apiMapper::toSportDto).toList();
    }

    public TournamentDtos.SportDto createSport(TournamentDtos.SportUpsertRequest request) {
        Sport sport = new Sport();
        sport.setId("s-" + UUID.randomUUID());
        applyUpdate(sport, request);
        return apiMapper.toSportDto(sportRepository.save(sport));
    }

    public TournamentDtos.SportDto updateSport(String id, TournamentDtos.SportUpsertRequest request) {
        Sport sport = sportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        applyUpdate(sport, request);
        return apiMapper.toSportDto(sportRepository.save(sport));
    }

    public Map<String, Boolean> deleteSport(String id) {
        if (!sportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sport not found");
        }
        sportRepository.deleteById(id);
        return Map.of("success", true);
    }

    private void applyUpdate(Sport sport, TournamentDtos.SportUpsertRequest request) {
        agePolicyService.validateSportAgeRange(request.minAge(), request.maxAge());
        sport.setName(request.name());
        sport.setIcon(normalizeIcon(request.icon()));
        sport.setDescription(request.description());
        sport.setScoreFieldsJson(apiMapper.writeJson(request.scoreFields() == null ? List.of() : request.scoreFields()));
        sport.setRankingWin(request.rankingPoints() == null ? 3 : request.rankingPoints().win());
        sport.setRankingDraw(request.rankingPoints() == null ? 1 : request.rankingPoints().draw());
        sport.setRankingLoss(request.rankingPoints() == null ? 0 : request.rankingPoints().loss());
        sport.setMinAge(request.minAge());
        sport.setMaxAge(request.maxAge());
    }

    private String normalizeIcon(String icon) {
        return icon == null || icon.isBlank() ? DEFAULT_SPORT_ICON : icon.trim();
    }
}
