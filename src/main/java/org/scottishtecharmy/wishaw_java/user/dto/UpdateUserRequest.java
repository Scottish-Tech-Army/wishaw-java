package org.scottishtecharmy.wishaw_java.user.dto;

import org.scottishtecharmy.wishaw_java.common.ValidDob;

import java.time.LocalDate;

public record UpdateUserRequest(
        String displayName,
        String role,
        Long centreId,
        @ValidDob
        LocalDate dob
) { }
