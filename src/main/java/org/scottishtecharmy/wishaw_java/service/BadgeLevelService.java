package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.BadgeLevelDto;
import org.scottishtecharmy.wishaw_java.dto.MainBadgeSummaryDto;
import org.scottishtecharmy.wishaw_java.model.BadgeLevel;
import org.scottishtecharmy.wishaw_java.model.MainBadge;
import org.scottishtecharmy.wishaw_java.model.StudentSubBadge;
import org.scottishtecharmy.wishaw_java.repository.BadgeLevelRepository;
import org.scottishtecharmy.wishaw_java.repository.StudentSubBadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Shared helper that resolves badge levels and builds badge summary DTOs.
 * Used by DashboardService, BadgeCatalogueService, TeamService, etc.
 */
@Service
public class BadgeLevelService {

    private final BadgeLevelRepository badgeLevelRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;

    public BadgeLevelService(BadgeLevelRepository badgeLevelRepository,
                             StudentSubBadgeRepository studentSubBadgeRepository) {
        this.badgeLevelRepository = badgeLevelRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
    }

    public List<BadgeLevelDto> getAllBadgeLevels() {
        return badgeLevelRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public BadgeLevelDto toDto(BadgeLevel bl) {
        return BadgeLevelDto.builder()
                .name(bl.getName())
                .label(bl.getLabel())
                .minXP(bl.getMinXP())
                .maxXP(bl.getMaxXP())
                .color(bl.getColor())
                .icon(bl.getIcon())
                .build();
    }

    /** Resolve which badge level applies for the given XP total. */
    public BadgeLevel resolveLevel(int xpEarned) {
        List<BadgeLevel> levels = badgeLevelRepository.findAllByOrderBySortOrderAsc();
        BadgeLevel resolved = levels.get(0); // default to first (Bronze)
        for (BadgeLevel level : levels) {
            if (xpEarned >= level.getMinXP()) {
                resolved = level;
            }
        }
        return resolved;
    }

    /**
     * Build a MainBadgeSummaryDto for a given student + main badge,
     * computing XP earned and sub-badge counts from the student's progress.
     */
    public MainBadgeSummaryDto buildSummary(Long studentId, MainBadge mainBadge) {
        List<StudentSubBadge> progress = studentSubBadgeRepository
                .findByStudentIdAndSubBadgeMainBadgeId(studentId, mainBadge.getId());

        int xpEarned = progress.stream()
                .filter(StudentSubBadge::isEarned)
                .mapToInt(ssb -> ssb.getSubBadge().getXpReward())
                .sum();

        int subBadgesEarned = (int) progress.stream().filter(StudentSubBadge::isEarned).count();
        int subBadgesTotal = mainBadge.getSubBadges().size();

        BadgeLevel level = resolveLevel(xpEarned);

        return MainBadgeSummaryDto.builder()
                .id(mainBadge.getSlug())
                .icon(mainBadge.getIcon())
                .name(mainBadge.getName())
                .xpEarned(xpEarned)
                .levelName(level.getName().toUpperCase())
                .levelLabel(level.getLabel())
                .levelColor(level.getColor())
                .levelIcon(level.getIcon())
                .subBadgesEarned(subBadgesEarned)
                .subBadgesTotal(subBadgesTotal)
                .build();
    }
}
