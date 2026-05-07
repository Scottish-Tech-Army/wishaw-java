package org.scottishtecharmy.wishaw_java.controller;

import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.CreateChallengeRequest;
import org.scottishtecharmy.wishaw_java.dto.request.CreateModuleRequest;
import org.scottishtecharmy.wishaw_java.dto.request.CreateScheduleItemRequest;
import org.scottishtecharmy.wishaw_java.dto.response.ModuleDetailResponse;
import org.scottishtecharmy.wishaw_java.dto.response.ModuleSummaryResponse;
import org.scottishtecharmy.wishaw_java.service.admin.ModuleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ModuleAdminController {

    private final ModuleAdminService moduleAdminService;

    @PostMapping("/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public ModuleSummaryResponse createModule(@Valid @RequestBody CreateModuleRequest request, Authentication authentication) {
        return moduleAdminService.createModule(request, authentication.getName());
    }

    @GetMapping("/modules")
    public List<ModuleSummaryResponse> listModules() {
        return moduleAdminService.listModules();
    }

    @GetMapping("/modules/{id}")
    public ModuleDetailResponse getModule(@PathVariable Long id) {
        return moduleAdminService.getModule(id);
    }

    @PutMapping("/modules/{id}")
    public ModuleSummaryResponse updateModule(@PathVariable Long id, @Valid @RequestBody CreateModuleRequest request) {
        return moduleAdminService.updateModule(id, request);
    }

    @DeleteMapping("/modules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteModule(@PathVariable Long id) {
        moduleAdminService.deleteModule(id);
    }

    @PostMapping("/modules/{id}/challenges")
    public ModuleDetailResponse createChallenge(@PathVariable Long id, @Valid @RequestBody CreateChallengeRequest request) {
        return moduleAdminService.createChallenge(id, request);
    }

    @PutMapping("/challenges/{id}")
    public ModuleDetailResponse updateChallenge(@PathVariable Long id, @Valid @RequestBody CreateChallengeRequest request) {
        return moduleAdminService.updateChallenge(id, request);
    }

    @DeleteMapping("/challenges/{id}")
    public ModuleDetailResponse deleteChallenge(@PathVariable Long id) {
        return moduleAdminService.deleteChallenge(id);
    }

    @PostMapping("/modules/{id}/schedule-items")
    public ModuleDetailResponse createScheduleItem(@PathVariable Long id, @RequestBody CreateScheduleItemRequest request) {
        return moduleAdminService.createScheduleItem(id, request);
    }

    @PutMapping("/schedule-items/{id}")
    public ModuleDetailResponse updateScheduleItem(@PathVariable Long id, @RequestBody CreateScheduleItemRequest request) {
        return moduleAdminService.updateScheduleItem(id, request);
    }
}