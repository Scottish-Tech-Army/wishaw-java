package org.scottishtecharmy.wishaw_java.mapper;

import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.dto.response.CentreResponse;
import org.scottishtecharmy.wishaw_java.dto.response.EnrollmentResponse;
import org.scottishtecharmy.wishaw_java.dto.response.GroupResponse;
import org.scottishtecharmy.wishaw_java.dto.response.ModuleDetailResponse;
import org.scottishtecharmy.wishaw_java.dto.response.ModuleSummaryResponse;
import org.scottishtecharmy.wishaw_java.dto.response.ParentLinkResponse;
import org.scottishtecharmy.wishaw_java.dto.response.PlayerProfileResponse;
import org.scottishtecharmy.wishaw_java.dto.response.UserSummaryResponse;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.Group;
import org.scottishtecharmy.wishaw_java.entity.Module;
import org.scottishtecharmy.wishaw_java.entity.ModuleScheduleItem;
import org.scottishtecharmy.wishaw_java.entity.ParentLink;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.scottishtecharmy.wishaw_java.entity.PlayerModuleEnrollment;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;

import java.util.List;

/**
 * Manual DTO mapper — no MapStruct or ModelMapper per project constraints.
 */
public final class DtoMapper {

    private DtoMapper() {}

    public static UserSummaryResponse toUserSummary(UserAccount u) {
        return UserSummaryResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .displayName(u.getDisplayName())
                .role(u.getRole().name())
                .active(u.isActive())
                .centreId(u.getCentre() != null ? u.getCentre().getId() : null)
                .centreName(u.getCentre() != null ? u.getCentre().getName() : null)
                .groupId(u.getGroup() != null ? u.getGroup().getId() : null)
                .groupName(u.getGroup() != null ? u.getGroup().getName() : null)
                .externalRef(u.getExternalRef())
                .build();
    }

    public static CentreResponse toCentreResponse(Centre c) {
        return CentreResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .code(c.getCode())
                .active(c.isActive())
                .build();
    }

    public static GroupResponse toGroupResponse(Group g) {
        return GroupResponse.builder()
                .id(g.getId())
                .name(g.getName())
                .gameName(g.getGameName())
                .ageBand(g.getAgeBand())
                .centreId(g.getCentre().getId())
                .centreName(g.getCentre().getName())
                .active(g.isActive())
                .build();
    }

    public static ModuleSummaryResponse toModuleSummary(Module m) {
        return ModuleSummaryResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .gameName(m.getGameName())
                .description(m.getDescription())
                .active(m.isActive())
                .approved(m.isApproved())
                .build();
    }

    public static ModuleDetailResponse toModuleDetail(Module m, List<Challenge> challenges, List<ModuleScheduleItem> items) {
        List<ModuleDetailResponse.ChallengeResponse> challengeResponses = challenges.stream()
                .map(c -> ModuleDetailResponse.ChallengeResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .points(c.getPoints())
                        .badgeCategoryId(c.getBadgeCategory().getId())
                        .badgeCategoryName(c.getBadgeCategory().getDisplayName())
                        .displayOrder(c.getDisplayOrder())
                        .active(c.isActive())
                        .build())
                .toList();

        List<ModuleDetailResponse.ScheduleItemResponse> scheduleResponses = items.stream()
                .map(s -> ModuleDetailResponse.ScheduleItemResponse.builder()
                        .id(s.getId())
                        .weekNumber(s.getWeekNumber())
                        .sessionFocus(s.getSessionFocus())
                        .linkedChallengeId(s.getLinkedChallenge() != null ? s.getLinkedChallenge().getId() : null)
                        .sessionPlanUrl(s.getSessionPlanUrl())
                        .sessionSlidesUrl(s.getSessionSlidesUrl())
                        .displayOrder(s.getDisplayOrder())
                        .build())
                .toList();

        return ModuleDetailResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .gameName(m.getGameName())
                .description(m.getDescription())
                .active(m.isActive())
                .approved(m.isApproved())
                .challenges(challengeResponses)
                .scheduleItems(scheduleResponses)
                .build();
    }

    public static BadgeProgressResponse toBadgeProgress(PlayerBadgeProgress p) {
        return BadgeProgressResponse.builder()
                .badgeCategoryId(p.getBadgeCategory().getId())
                .categoryCode(p.getBadgeCategory().getCode())
                .categoryName(p.getBadgeCategory().getDisplayName())
                .legacyPoints(p.getLegacyPoints())
                .earnedPoints(p.getEarnedPoints())
                .totalPoints(p.getTotalPoints())
                .currentLevel(p.getCurrentLevelName())
                .build();
    }

    public static PlayerProfileResponse toPlayerProfile(UserAccount player, List<PlayerBadgeProgress> progressList) {
        List<BadgeProgressResponse> badges = progressList.stream()
                .map(DtoMapper::toBadgeProgress)
                .toList();
        int overall = badges.stream().mapToInt(BadgeProgressResponse::getTotalPoints).sum();

        return PlayerProfileResponse.builder()
                .id(player.getId())
                .username(player.getUsername())
                .displayName(player.getDisplayName())
                .centreName(player.getCentre() != null ? player.getCentre().getName() : null)
                .groupName(player.getGroup() != null ? player.getGroup().getName() : null)
                .badgeProgress(badges)
                .overallTotalPoints(overall)
                .build();
    }

    public static EnrollmentResponse toEnrollmentResponse(PlayerModuleEnrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .playerId(e.getPlayer().getId())
                .playerName(e.getPlayer().getDisplayName())
                .moduleId(e.getModule().getId())
                .moduleName(e.getModule().getName())
                .status(e.getStatus().name())
                .build();
    }

    public static ParentLinkResponse toParentLinkResponse(ParentLink link) {
        return ParentLinkResponse.builder()
                .id(link.getId())
                .parentUserId(link.getParentUser().getId())
                .parentDisplayName(link.getParentUser().getDisplayName())
                .playerUserId(link.getPlayerUser().getId())
                .playerDisplayName(link.getPlayerUser().getDisplayName())
                .relationshipLabel(link.getRelationshipLabel())
                .build();
    }
}
