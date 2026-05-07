package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.dto.response.PlayerProfileResponse;
import org.scottishtecharmy.wishaw_java.service.player.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final ProgressService progressService;

    @GetMapping("/profile")
    public PlayerProfileResponse getMyProfile(Authentication authentication) {
        return progressService.getCurrentPlayerProfile(authentication.getName());
    }

    @GetMapping("/progress")
    public List<BadgeProgressResponse> getMyProgress(Authentication authentication) {
        return progressService.getCurrentPlayerProgress(authentication.getName());
    }
}