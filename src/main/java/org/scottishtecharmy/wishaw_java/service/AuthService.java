package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.AuthDtos;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.repository.UserProfileRepository;
import org.scottishtecharmy.wishaw_java.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final CentreRepository centreRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApiMapper apiMapper;
    private final AgePolicyService agePolicyService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserAccountRepository userAccountRepository,
                       UserProfileRepository userProfileRepository,
                       CentreRepository centreRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       ApiMapper apiMapper,
                       AgePolicyService agePolicyService) {
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.centreRepository = centreRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.apiMapper = apiMapper;
        this.agePolicyService = agePolicyService;
    }

    public AuthDtos.AuthResponseDto login(AuthDtos.LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException exception) {
            throw new BadRequestException("Invalid email or password");
        }

        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildAuthResponse(userAccount, getProfile(userAccount.getId()));
    }

    public AuthDtos.AuthResponseDto register(AuthDtos.RegisterRequest request) {
        if (request.email() == null || request.email().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Email and password are required");
        }
        if (userAccountRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new BadRequestException("Email address is already registered");
        }

        Centre centre = centreRepository.findById("c1")
                .or(() -> centreRepository.findAll().stream().findFirst())
                .orElse(null);
        LocalDate dateOfBirth = agePolicyService.parseDateOfBirth(request.dateOfBirth(), true);

        UserAccount userAccount = UserAccount.builder()
                .id("u" + System.currentTimeMillis())
                .email(request.email().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.PLAYER)
                .centre(centre)
                .build();
        userAccount = userAccountRepository.save(userAccount);

        UserProfile userProfile = UserProfile.builder()
                .userAccount(userAccount)
                .displayName(request.displayName())
                .firstName(request.firstName())
                .lastName(request.lastName())
            .dateOfBirth(dateOfBirth)
                .bio("")
                .photoUrl(null)
                .overlayTemplate(null)
                .showInPublicList(true)
                .allowSocialSharing(true)
                .build();
        userProfileRepository.save(userProfile);

        return buildAuthResponse(userAccount, userProfile);
    }

    @Transactional(readOnly = true)
    public AuthDtos.SessionDto getSession(UserAccount userAccount) {
        return new AuthDtos.SessionDto(apiMapper.toUserDto(userAccount), apiMapper.toProfileDto(getProfile(userAccount.getId())));
    }

    public AuthDtos.RefreshTokenResponse refresh(AuthDtos.RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        if (refreshToken == null || refreshToken.isBlank() || !jwtService.isValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        UserAccount userAccount = userAccountRepository.findById(jwtService.extractSubject(refreshToken))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new AuthDtos.RefreshTokenResponse(
                jwtService.generateAccessToken(userAccount),
                jwtService.generateRefreshToken(userAccount)
        );
    }

    public void logout() {
        // Stateless JWT logout is handled client-side by dropping tokens.
    }

    private AuthDtos.AuthResponseDto buildAuthResponse(UserAccount userAccount, UserProfile userProfile) {
        return new AuthDtos.AuthResponseDto(
                jwtService.generateAccessToken(userAccount),
                jwtService.generateRefreshToken(userAccount),
                apiMapper.toUserDto(userAccount),
                apiMapper.toProfileDto(userProfile)
        );
    }

    private UserProfile getProfile(String userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }
}
