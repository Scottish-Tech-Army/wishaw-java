package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.dto.response.PlayerProfileResponse;
import org.scottishtecharmy.wishaw_java.service.player.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final ProgressService progressService;

    // FRONTEND_INTEGRATION: This DTO is used by the React player profile page.
    // FRONTEND_INTEGRATION: Do not rename JSON fields without updating frontend contract.
    @GetMapping("/{playerId}/profile")
    public PlayerProfileResponse getPlayerProfile(@PathVariable Long playerId, Authentication authentication) {
        return progressService.getPlayerProfile(playerId, authentication.getName());
    }

    @GetMapping("/{playerId}/progress")
    public List<BadgeProgressResponse> getPlayerProgress(@PathVariable Long playerId, Authentication authentication) {
        return progressService.getPlayerProgress(playerId, authentication.getName());
    }
}
