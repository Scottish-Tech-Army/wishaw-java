package org.scottishtecharmy.wishaw_java.controller;

import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.AwardChallengeRequest;
import org.scottishtecharmy.wishaw_java.dto.request.SetLegacyPointsRequest;
import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.service.player.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/progress")
@RequiredArgsConstructor
public class ProgressAdminController {

    private final ProgressService progressService;

    @PostMapping("/legacy-points")
    public BadgeProgressResponse setLegacyPoints(@Valid @RequestBody SetLegacyPointsRequest request,
                                                 Authentication authentication) {
        return progressService.setLegacyPoints(request, authentication.getName());
    }

    @PostMapping("/award-challenge")
    public BadgeProgressResponse awardChallenge(@Valid @RequestBody AwardChallengeRequest request, Authentication authentication) {
        return progressService.awardChallenge(request, authentication.getName());
    }
}
