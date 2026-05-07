package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.CatalogueDtos;
import org.scottishtecharmy.wishaw_java.service.CatalogueService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Tag(name = "Catalogue", description = "Centres, groups, badges, and modules")
public class CatalogueController {

    private final CatalogueService catalogueService;

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping({ApiPaths.V1 + "/centres", ApiPaths.LEGACY + "/centres"})
    public List<CatalogueDtos.CentreDto> getCentres() {
        return catalogueService.getCentres();
    }

    @GetMapping({ApiPaths.V1 + "/groups", ApiPaths.LEGACY + "/groups"})
    public List<CatalogueDtos.GroupDto> getGroups(@RequestParam(required = false) String centreId) {
        return catalogueService.getGroups(centreId);
    }

    @GetMapping({ApiPaths.V1 + "/badges/main", ApiPaths.LEGACY + "/badges/main"})
    public List<CatalogueDtos.MainBadgeDto> getMainBadges() {
        return catalogueService.getMainBadges();
    }

    @GetMapping({ApiPaths.V1 + "/badges/sub", ApiPaths.LEGACY + "/badges/sub"})
    public List<CatalogueDtos.SubBadgeDto> getSubBadges(@RequestParam(required = false) String moduleId) {
        return catalogueService.getSubBadges(moduleId);
    }

    @GetMapping({ApiPaths.V1 + "/badges/progress/{userId}", ApiPaths.LEGACY + "/badges/progress/{userId}"})
    public List<CatalogueDtos.UserBadgeProgressDto> getProgress(@PathVariable String userId) {
        return catalogueService.getUserBadgeProgress(userId);
    }

    @PostMapping({ApiPaths.V1 + "/badges/award", ApiPaths.LEGACY + "/badges/award"})
    public Map<String, Boolean> award(@RequestBody CatalogueDtos.BadgeAwardRequest request) {
        return catalogueService.awardSubBadge(request.userId(), request.subBadgeId());
    }

    @GetMapping({ApiPaths.V1 + "/modules", ApiPaths.LEGACY + "/modules"})
    public List<CatalogueDtos.ModuleDto> getModules() {
        return catalogueService.getModules();
    }

    @GetMapping({ApiPaths.V1 + "/modules/{id}", ApiPaths.LEGACY + "/modules/{id}"})
    public CatalogueDtos.ModuleDto getModule(@PathVariable String id) {
        return catalogueService.getModule(id);
    }

    @PostMapping({ApiPaths.V1 + "/modules", ApiPaths.LEGACY + "/modules"})
    public CatalogueDtos.ModuleDto createModule(@RequestBody CatalogueDtos.ModuleUpsertRequest request) {
        return catalogueService.createModule(request);
    }

    @PutMapping({ApiPaths.V1 + "/modules/{id}", ApiPaths.LEGACY + "/modules/{id}"})
    public CatalogueDtos.ModuleDto updateModule(@PathVariable String id, @RequestBody CatalogueDtos.ModuleUpsertRequest request) {
        return catalogueService.updateModule(id, request);
    }

    @DeleteMapping({ApiPaths.V1 + "/modules/{id}", ApiPaths.LEGACY + "/modules/{id}"})
    public Map<String, Boolean> deleteModule(@PathVariable String id) {
        return catalogueService.deleteModule(id);
    }
}
