package org.scottishtecharmy.wishaw_java.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.scottishtecharmy.wishaw_java.common.ValidDob;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 50)
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 100)
        String password,

        String displayName,

        String role,       // MAIN_ADMIN, CENTRE_ADMIN, USER

        Long centreId,

        @ValidDob
        LocalDate dob
) { }
