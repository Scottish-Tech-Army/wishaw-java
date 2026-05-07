package org.scottishtecharmy.wishaw_java.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.auth.dto.AuthResponse;
import org.scottishtecharmy.wishaw_java.auth.dto.LoginRequest;
import org.scottishtecharmy.wishaw_java.auth.dto.RegisterRequest;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.user.Role;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CentreRepository centreRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for username='{}'", request.username());
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed — username '{}' already exists", request.username());
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName() != null ? request.displayName() : request.username());
        user.setRole(request.role() != null ? Role.valueOf(request.role()) : Role.USER);
        user.setDob(request.dob());

        if (request.centreId() != null) {
            Centre centre = centreRepository.findById(request.centreId())
                    .orElseThrow(() -> new IllegalArgumentException("Centre not found"));
            user.setCentre(centre);
        }

        userRepository.save(user);
        log.info("User registered successfully: id={}, username='{}', role={}", user.getId(), user.getUsername(), user.getRole());

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name(),
                user.getCentre() != null ? user.getCentre().getId() : null);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username='{}'", request.username());
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.warn("Login failed — username '{}' not found", request.username());
                    return new IllegalArgumentException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed — incorrect password for username='{}'", request.username());
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        log.info("Login successful: userId={}, username='{}', role={}", user.getId(), user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name(),
                user.getCentre() != null ? user.getCentre().getId() : null);
    }
}
