package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final XpEventRepository xpEventRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;
    private final BadgeLevelService badgeLevelService;

    public DashboardService(StudentRepository studentRepository,
                            MainBadgeRepository mainBadgeRepository,
                            XpEventRepository xpEventRepository,
                            StudentSubBadgeRepository studentSubBadgeRepository,
                            BadgeLevelService badgeLevelService) {
        this.studentRepository = studentRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.xpEventRepository = xpEventRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
        this.badgeLevelService = badgeLevelService;
    }

    public DashboardSummaryDto getDashboard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // Badge summaries
        List<MainBadge> mainBadges = mainBadgeRepository.findAll();
        List<MainBadgeSummaryDto> badgeSummaries = mainBadges.stream()
                .map(mb -> badgeLevelService.buildSummary(studentId, mb))
                .toList();

        // Sub-badge counts
        List<StudentSubBadge> allProgress = studentSubBadgeRepository.findByStudentId(studentId);
        int earnedSubBadges = (int) allProgress.stream().filter(StudentSubBadge::isEarned).count();
        int totalSubBadges = allProgress.size();

        // Recent activity (last 5)
        List<XpEventDto> recentActivity = xpEventRepository.findTop5ByStudentIdOrderByDateDesc(studentId)
                .stream()
                .map(this::toXpEventDto)
                .toList();

        // Leaderboard rank — simple position by XP descending
        List<Student> allStudents = studentRepository.findAllByOrderByXpDesc();
        Integer rank = null;
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(studentId)) {
                rank = i + 1;
                break;
            }
        }

        // XP for next level — simple formula: (level + 1) * 200
        int xpForNextLevel = (student.getLevel() + 1) * 200;

        // Weekly XP — simplified: sum of all XP events (in real app, filter by date range)
        int weeklyXp = recentActivity.stream().mapToInt(XpEventDto::getXp).sum();

        // Team/hub weekly XP — simplified mock values
        int teamWeeklyXp = weeklyXp * 3;
        int hubWeeklyXp = weeklyXp * 8;

        // Team info
        Team team = student.getTeam();

        return DashboardSummaryDto.builder()
                .studentId(student.getId())
                .gamertag(student.getGamertag())
                .name(student.getRealName())
                .username(student.getUsername())
                .avatarUrl(student.getAvatarUrl())
                .bio(student.getBio())
                .joinedDate(student.getJoinedDate())
                .hub(student.getCentre() != null ? student.getCentre().getName() : null)
                .level(student.getLevel())
                .xp(student.getXp())
                .xpForNextLevel(xpForNextLevel)
                .weeklyXp(weeklyXp)
                .teamWeeklyXp(teamWeeklyXp)
                .hubWeeklyXp(hubWeeklyXp)
                .totalSubBadges(totalSubBadges)
                .earnedSubBadges(earnedSubBadges)
                .leaderboardRank(rank)
                .nextSessionAt(null) // Not yet implemented
                .teamName(team != null ? team.getName() : null)
                .teamIcon(team != null ? team.getIcon() : null)
                .teamId(team != null ? team.getSlug() : null)
                .teamColour(team != null ? team.getColour() : null)
                .isCaptain(student.isCaptain())
                .badges(badgeSummaries)
                .recentActivity(recentActivity)
                .build();
    }

    private XpEventDto toXpEventDto(XpEvent event) {
        return XpEventDto.builder()
                .id(event.getId())
                .activity(event.getActivity())
                .xp(event.getXp())
                .date(event.getDate())
                .icon(event.getIcon())
                .build();
    }
}
