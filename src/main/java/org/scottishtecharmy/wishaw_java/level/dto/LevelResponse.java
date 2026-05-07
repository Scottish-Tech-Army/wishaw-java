package org.scottishtecharmy.wishaw_java.level.dto;

import java.time.LocalDateTime;

public record LevelResponse(
        Long id,
        String name,
        int minPoints,
        int maxPoints,
        int displayOrder,
        LocalDateTime createdAt
) { }

