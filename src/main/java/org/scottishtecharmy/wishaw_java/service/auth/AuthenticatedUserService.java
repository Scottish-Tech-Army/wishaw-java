package org.scottishtecharmy.wishaw_java.service.auth;

import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.exception.UnauthorizedException;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserAccountRepository userAccountRepository;

    public UserAccount getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedException("Authentication is required");
        }

        return userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
    }

    public UserAccount getByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found: " + username));
    }
}