package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class BadgeCatalogueService {

    private final StudentRepository studentRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;
    private final BadgeLevelService badgeLevelService;

    public BadgeCatalogueService(StudentRepository studentRepository,
                                 MainBadgeRepository mainBadgeRepository,
                                 StudentSubBadgeRepository studentSubBadgeRepository,
                                 BadgeLevelService badgeLevelService) {
        this.studentRepository = studentRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
        this.badgeLevelService = badgeLevelService;
    }

    public BadgeCatalogueDto getBadgeCatalogue(Long studentId) {
        List<MainBadge> mainBadges = mainBadgeRepository.findAll();
        List<StudentSubBadge> allProgress = studentSubBadgeRepository.findByStudentId(studentId);

        List<MainBadgeDetailDto> badges = mainBadges.stream()
                .map(mb -> buildMainBadgeDetail(mb, allProgress))
                .toList();

        return BadgeCatalogueDto.builder()
                .badgeLevels(badgeLevelService.getAllBadgeLevels())
                .badges(badges)
                .build();
    }

    public PublicBadgeSummaryDto getPublicBadgeSummary(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        List<MainBadge> mainBadges = mainBadgeRepository.findAll();
        List<MainBadgeSummaryDto> badges = mainBadges.stream()
                .map(mb -> badgeLevelService.buildSummary(student.getId(), mb))
                .toList();

        return PublicBadgeSummaryDto.builder().badges(badges).build();
    }

    private MainBadgeDetailDto buildMainBadgeDetail(MainBadge mb, List<StudentSubBadge> allProgress) {
        List<SubBadgeDetailDto> subBadgeDetails = mb.getSubBadges().stream().map(sb -> {
            boolean earned = allProgress.stream()
                    .anyMatch(ssb -> ssb.getSubBadge().getId().equals(sb.getId()) && ssb.isEarned());
            String earnedDate = allProgress.stream()
                    .filter(ssb -> ssb.getSubBadge().getId().equals(sb.getId()) && ssb.isEarned())
                    .findFirst()
                    .map(StudentSubBadge::getEarnedDate)
                    .orElse(null);

            List<String> skills = sb.getSkills() != null
                    ? Arrays.asList(sb.getSkills().split(",\\s*"))
                    : List.of();

            return SubBadgeDetailDto.builder()
                    .id(sb.getId())
                    .icon(sb.getIcon())
                    .name(sb.getName())
                    .shortDesc(sb.getShortDesc())
                    .criteria(sb.getCriteria())
                    .xpReward(sb.getXpReward())
                    .type(sb.getType())
                    .skills(skills)
                    .earned(earned)
                    .earnedDate(earnedDate)
                    .build();
        }).toList();

        int xpEarned = subBadgeDetails.stream()
                .filter(SubBadgeDetailDto::isEarned)
                .mapToInt(SubBadgeDetailDto::getXpReward)
                .sum();

        return MainBadgeDetailDto.builder()
                .id(mb.getSlug())
                .icon(mb.getIcon())
                .name(mb.getName())
                .tagline(mb.getTagline())
                .description(mb.getDescription())
                .xpEarned(xpEarned)
                .subBadges(subBadgeDetails)
                .build();
    }
}
