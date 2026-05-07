package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.AuthDtos;
import org.scottishtecharmy.wishaw_java.service.AuthService;
import org.scottishtecharmy.wishaw_java.service.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping({ApiPaths.V1 + "/auth", ApiPaths.LEGACY + "/auth"})
@Tag(name = "Auth", description = "Authentication and session endpoints")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", security = {})
    public AuthDtos.AuthResponseDto login(@RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new player account", security = {})
    public AuthDtos.AuthResponseDto register(@RequestBody AuthDtos.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access and refresh tokens", security = {})
    public AuthDtos.RefreshTokenResponse refresh(@RequestBody AuthDtos.RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current session")
    public Map<String, Boolean> logout() {
        authService.logout();
        return Map.of("success", true);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated session")
    public AuthDtos.SessionDto me() {
        return authService.getSession(currentUserService.requireCurrentUser());
    }
}
