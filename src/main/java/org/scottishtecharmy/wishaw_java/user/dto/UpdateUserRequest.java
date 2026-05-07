package org.scottishtecharmy.wishaw_java.user.dto;

public record UpdateUserRequest(
        String displayName,
        String role,
        Long centreId
) { }

