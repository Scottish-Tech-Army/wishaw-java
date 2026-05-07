package org.scottishtecharmy.wishaw_java.service;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.LoginRequestDTO;
import org.scottishtecharmy.wishaw_java.dto.LoginResponseDTO;
import org.scottishtecharmy.wishaw_java.dto.UserDTO;
import org.scottishtecharmy.wishaw_java.dto.UserResponseDTO;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import com.ltc.exception.*;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.exception.UnauthorizedException;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.scottishtecharmy.wishaw_java.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponseDTO register(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .role(dto.getRole() != null ? dto.getRole() : UserRole.PLAYER)
                .organization(dto.getOrganization())
                .phone(dto.getPhone())
                .profilePhotoUrl(dto.getProfilePhotoUrl())
                .build();

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );

            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(dto.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return LoginResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    private UserResponseDTO mapToResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .organization(user.getOrganization())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }
}

