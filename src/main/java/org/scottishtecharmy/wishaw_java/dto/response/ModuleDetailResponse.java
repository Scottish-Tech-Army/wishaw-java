package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ModuleDetailResponse {
    private Long id;
    private String name;
    private String gameName;
    private String description;
    private boolean active;
    private boolean approved;
    private List<ChallengeResponse> challenges;
    private List<ScheduleItemResponse> scheduleItems;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ChallengeResponse {
        private Long id;
        private String name;
        private String description;
        private int points;
        private Long badgeCategoryId;
        private String badgeCategoryName;
        private int displayOrder;
        private boolean active;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ScheduleItemResponse {
        private Long id;
        private int weekNumber;
        private String sessionFocus;
        private Long linkedChallengeId;
        private String sessionPlanUrl;
        private String sessionSlidesUrl;
        private int displayOrder;
    }
}
