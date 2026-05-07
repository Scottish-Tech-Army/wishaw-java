package org.scottishtecharmy.wishaw_java.dto;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(String email, String password) {
    }

    public record RegisterRequest(
            String email,
            String password,
            String displayName,
            String firstName,
            String lastName,
            String dateOfBirth
    ) {
    }

    public record RefreshTokenRequest(String refreshToken) {
    }

    public record OverlayRequest(String template) {
    }

    public record PrivacyDto(boolean showInPublicList, boolean allowSocialSharing) {
    }

    public record UserDto(String id, String email, String role, String centreId) {
    }

    public record ProfileDto(
            String displayName,
            String firstName,
            String lastName,
            String dateOfBirth,
            String bio,
            String photoUrl,
            String overlayTemplate,
            PrivacyDto privacy
    ) {
    }

    public record AuthResponseDto(
            String accessToken,
            String refreshToken,
            UserDto user,
            ProfileDto profile
    ) {
    }

        public record SessionDto(UserDto user, ProfileDto profile) {
        }

    public record RefreshTokenResponse(String accessToken, String refreshToken) {
    }
}
