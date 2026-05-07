package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * // FRONTEND_INTEGRATION: React login screen will receive this after successful authentication.
 * // FRONTEND_INTEGRATION: Keep response shape stable for future frontend session handling.
 */
@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private String displayName;
    private String role;
    private String message;
}
