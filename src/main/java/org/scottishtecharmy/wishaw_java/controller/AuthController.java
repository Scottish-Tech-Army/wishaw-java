package org.scottishtecharmy.wishaw_java.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.LoginRequest;
import org.scottishtecharmy.wishaw_java.dto.response.AuthResponse;
import org.scottishtecharmy.wishaw_java.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // FRONTEND_INTEGRATION: React login screen will post username/password here.
    // FRONTEND_INTEGRATION: Keep response shape stable for future frontend session handling.
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        return authService.me(authentication);
    }
}