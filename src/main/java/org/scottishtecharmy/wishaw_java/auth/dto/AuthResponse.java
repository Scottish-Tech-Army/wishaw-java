package org.scottishtecharmy.wishaw_java.auth.dto;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String role,
        Long centreId
) { }
