package org.scottishtecharmy.wishaw_java.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String displayName,
        String role,
        Long centreId,
        String centreName,
        String profileImageUrl,
        LocalDate dob,
        LocalDateTime createdAt
) { }
