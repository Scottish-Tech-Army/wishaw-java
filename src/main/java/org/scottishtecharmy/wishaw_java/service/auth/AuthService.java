package org.scottishtecharmy.wishaw_java.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.scottishtecharmy.wishaw_java.dto.request.LoginRequest;
import org.scottishtecharmy.wishaw_java.dto.response.AuthResponse;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthenticatedUserService authenticatedUserService;

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        HttpSession existingSession = httpRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        UserAccount account = authenticatedUserService.getByUsername(authentication.getName());
        return toAuthResponse(account, "Login successful");
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    public AuthResponse me(Authentication authentication) {
        UserAccount account = authenticatedUserService.getCurrentUser(authentication);
        return toAuthResponse(account, "Authenticated user");
    }

    private AuthResponse toAuthResponse(UserAccount account, String message) {
        return AuthResponse.builder()
                .userId(account.getId())
                .username(account.getUsername())
                .displayName(account.getDisplayName())
                .role(account.getRole().name())
                .message(message)
                .build();
    }
}
