package org.scottishtecharmy.wishaw_java.centre.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CentreRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "code is required")
        @Size(max = 20)
        String code
) { }

