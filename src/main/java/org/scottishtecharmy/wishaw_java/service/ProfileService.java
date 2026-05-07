package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.AuthDtos;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

@Service
@Transactional
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ApiMapper apiMapper;
    private final AgePolicyService agePolicyService;

    public ProfileService(UserProfileRepository userProfileRepository, ApiMapper apiMapper, AgePolicyService agePolicyService) {
        this.userProfileRepository = userProfileRepository;
        this.apiMapper = apiMapper;
        this.agePolicyService = agePolicyService;
    }

    @Transactional(readOnly = true)
    public AuthDtos.ProfileDto getProfile(UserAccount currentUser) {
        return apiMapper.toProfileDto(requireProfile(currentUser.getId()));
    }

    public AuthDtos.ProfileDto updateProfile(UserAccount currentUser, AuthDtos.ProfileDto request) {
        UserProfile userProfile = requireProfile(currentUser.getId());
        LocalDate dateOfBirth = agePolicyService.parseDateOfBirth(request.dateOfBirth(), false);
        userProfile.setDisplayName(request.displayName());
        userProfile.setFirstName(request.firstName());
        userProfile.setLastName(request.lastName());
        userProfile.setDateOfBirth(dateOfBirth);
        userProfile.setBio(request.bio());
        userProfile.setPhotoUrl(request.photoUrl());
        userProfile.setOverlayTemplate(request.overlayTemplate());
        if (request.privacy() != null) {
            userProfile.setShowInPublicList(request.privacy().showInPublicList());
            userProfile.setAllowSocialSharing(request.privacy().allowSocialSharing());
        }
        return apiMapper.toProfileDto(userProfileRepository.save(userProfile));
    }

    public Map<String, String> uploadPhoto(UserAccount currentUser, MultipartFile file) {
        UserProfile userProfile = requireProfile(currentUser.getId());
        String photoUrl;
        if (file != null && !file.isEmpty()) {
            try {
                String mimeType = file.getContentType() != null ? file.getContentType() : "image/png";
                String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                photoUrl = "data:" + mimeType + ";base64," + base64;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read uploaded photo", e);
            }
        } else {
            photoUrl = buildAvatarUrl(userProfile, null);
        }
        userProfile.setPhotoUrl(photoUrl);
        userProfileRepository.save(userProfile);
        return Map.of("photoUrl", photoUrl);
    }

    public Map<String, String> setOverlay(UserAccount currentUser, String template) {
        UserProfile userProfile = requireProfile(currentUser.getId());
        userProfile.setOverlayTemplate(template);
        userProfileRepository.save(userProfile);
        return Map.of("overlayTemplate", template == null ? "" : template);
    }

    private UserProfile requireProfile(String userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private String buildAvatarUrl(UserProfile userProfile, String originalFilename) {
        String initials = initials(userProfile);
        String label = originalFilename == null || originalFilename.isBlank() ? initials : initials + " • " + originalFilename;
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='160' height='160' viewBox='0 0 160 160'>"
                + "<rect width='160' height='160' rx='24' fill='#0f766e'/>"
                + "<circle cx='80' cy='56' r='28' fill='#99f6e4'/>"
                + "<text x='80' y='120' text-anchor='middle' font-family='Verdana, sans-serif' font-size='26' fill='white'>"
                + escapeXml(label)
                + "</text></svg>";
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
    }

    private String initials(UserProfile userProfile) {
        String first = userProfile.getFirstName() == null ? "" : userProfile.getFirstName().trim();
        String last = userProfile.getLastName() == null ? "" : userProfile.getLastName().trim();
        String value = (first.isEmpty() ? "" : first.substring(0, 1)) + (last.isEmpty() ? "" : last.substring(0, 1));
        if (!value.isBlank()) {
            return value.toUpperCase();
        }
        return userProfile.getDisplayName() == null || userProfile.getDisplayName().isBlank()
                ? "P"
                : userProfile.getDisplayName().substring(0, 1).toUpperCase();
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
