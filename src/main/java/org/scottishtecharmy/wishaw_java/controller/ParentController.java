package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.dto.response.PlayerProfileResponse;
import org.scottishtecharmy.wishaw_java.dto.response.UserSummaryResponse;
import org.scottishtecharmy.wishaw_java.service.player.ParentReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentReadOnlyService parentReadOnlyService;

    @GetMapping("/players")
    public List<UserSummaryResponse> getLinkedPlayers(Authentication authentication) {
        return parentReadOnlyService.getLinkedPlayers(authentication.getName());
    }

    @GetMapping("/players/{playerId}/profile")
    public PlayerProfileResponse getLinkedPlayerProfile(@PathVariable Long playerId, Authentication authentication) {
        return parentReadOnlyService.getLinkedPlayerProfile(authentication.getName(), playerId);
    }

    @GetMapping("/players/{playerId}/progress")
    public List<BadgeProgressResponse> getLinkedPlayerProgress(@PathVariable Long playerId, Authentication authentication) {
        return parentReadOnlyService.getLinkedPlayerProgress(authentication.getName(), playerId);
    }
}