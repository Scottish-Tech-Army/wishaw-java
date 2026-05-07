package org.scottishtecharmy.wishaw_java.dto;

import java.util.List;

public final class CatalogueDtos {

    private CatalogueDtos() {
    }

    public record CentreDto(String id, String name, String location) {
    }

    public record GroupDto(String id, String name, String game, String centreId, String centreName, int memberCount) {
    }

    public record MainBadgeDto(String id, String name, String description, String icon) {
    }

    public record SubBadgeDto(
            String id,
            String name,
            String description,
            String mainBadgeId,
            String mainBadgeName,
            int points,
            List<String> skills,
            String moduleId
    ) {
    }

    public record UserBadgeProgressDto(
            String mainBadgeId,
            String mainBadgeName,
            int totalPoints,
            String level,
            List<String> earnedSubBadges
    ) {
    }

    public record ModuleSessionDto(
            int weekNo,
            String focus,
            String subBadgeId,
            String sessionPlanUrl,
            String slidesUrl
    ) {
    }

    public record ModuleDto(
            String id,
            String name,
            String game,
            String description,
            int durationWeeks,
            List<SubBadgeDto> subBadges,
            List<ModuleSessionDto> schedule,
            String status
    ) {
    }

    public record ModuleUpsertRequest(
            String name,
            String game,
            String description,
            Integer durationWeeks,
            String status
    ) {
    }

    public record BadgeAwardRequest(String userId, String subBadgeId) {
    }
}
