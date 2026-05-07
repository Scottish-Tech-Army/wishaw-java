package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BadgeProgressResponse {
    private Long badgeCategoryId;
    private String categoryCode;
    private String categoryName;
    private int legacyPoints;
    private int earnedPoints;
    private int totalPoints;
    private String currentLevel;
}
