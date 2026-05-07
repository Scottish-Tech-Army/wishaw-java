package org.scottishtecharmy.wishaw_java.centre.dto;

import java.time.LocalDateTime;

public record CentreResponse(
        Long id,
        String name,
        String code,
        LocalDateTime createdAt
) { }

