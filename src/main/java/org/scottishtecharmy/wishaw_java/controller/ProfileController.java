package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.AuthDtos;
import org.scottishtecharmy.wishaw_java.service.CurrentUserService;
import org.scottishtecharmy.wishaw_java.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping({ApiPaths.V1 + "/profile", ApiPaths.LEGACY + "/profile"})
@Tag(name = "Profile", description = "Current user profile endpoints")
public class ProfileController {

    private final ProfileService profileService;
    private final CurrentUserService currentUserService;

    public ProfileController(ProfileService profileService, CurrentUserService currentUserService) {
        this.profileService = profileService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public AuthDtos.ProfileDto getProfile() {
        return profileService.getProfile(currentUserService.requireCurrentUser());
    }

    @PutMapping
    public AuthDtos.ProfileDto updateProfile(@RequestBody AuthDtos.ProfileDto request) {
        return profileService.updateProfile(currentUserService.requireCurrentUser(), request);
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadPhoto(@RequestParam("photo") MultipartFile photo) {
        return profileService.uploadPhoto(currentUserService.requireCurrentUser(), photo);
    }

    @PostMapping("/photo/overlay")
    public Map<String, String> setOverlay(@RequestBody AuthDtos.OverlayRequest request) {
        return profileService.setOverlay(currentUserService.requireCurrentUser(), request.template());
    }
}
