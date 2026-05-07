package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.StudentSubBadge;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;

    public ModuleService(ModuleRepository moduleRepository,
                         StudentSubBadgeRepository studentSubBadgeRepository) {
        this.moduleRepository = moduleRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
    }

    public List<ModuleProgressDto> getModuleProgress(Long studentId) {
        List<org.scottishtecharmy.wishaw_java.model.Module> modules = moduleRepository.findAll();
        List<StudentSubBadge> allProgress = studentSubBadgeRepository.findByStudentId(studentId);

        return modules.stream().map(m -> {
            List<ModuleSubBadgeDto> subBadgeDtos = m.getSubBadges().stream().map(sb -> {
                boolean earned = allProgress.stream()
                        .anyMatch(ssb -> ssb.getSubBadge().getId().equals(sb.getId()) && ssb.isEarned());
                String earnedDate = allProgress.stream()
                        .filter(ssb -> ssb.getSubBadge().getId().equals(sb.getId()) && ssb.isEarned())
                        .findFirst()
                        .map(StudentSubBadge::getEarnedDate)
                        .orElse(null);

                return ModuleSubBadgeDto.builder()
                        .id(sb.getId())
                        .icon(sb.getIcon())
                        .name(sb.getName())
                        .desc(sb.getShortDesc())
                        .xpReward(sb.getXpReward())
                        .mainBadgeId(sb.getMainBadge().getSlug())
                        .earned(earned)
                        .earnedDate(earnedDate)
                        .build();
            }).collect(java.util.stream.Collectors.toList());

            return ModuleProgressDto.builder()
                    .id(m.getId())
                    .icon(m.getIcon())
                    .name(m.getName())
                    .outcome(m.getOutcome())
                    .durationWeeks(m.getDurationWeeks())
                    .subBadges(subBadgeDtos)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }
}
