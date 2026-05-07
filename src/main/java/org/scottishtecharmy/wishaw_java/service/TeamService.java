package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.MainBadge;
import org.scottishtecharmy.wishaw_java.model.Student;
import org.scottishtecharmy.wishaw_java.model.StudentSubBadge;
import org.scottishtecharmy.wishaw_java.model.Team;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final StudentRepository studentRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final ModuleRepository moduleRepository;
    private final BadgeLevelService badgeLevelService;

    public TeamService(TeamRepository teamRepository,
                       StudentRepository studentRepository,
                       StudentSubBadgeRepository studentSubBadgeRepository,
                       MainBadgeRepository mainBadgeRepository,
                       ModuleRepository moduleRepository,
                       BadgeLevelService badgeLevelService) {
        this.teamRepository = teamRepository;
        this.studentRepository = studentRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.moduleRepository = moduleRepository;
        this.badgeLevelService = badgeLevelService;
    }

    public List<TeamSummaryDto> getAllTeams() {
        return teamRepository.findAll().stream().map(team -> {
            List<Student> members = studentRepository.findByTeamId(team.getId());
            String captainGamertag = members.stream()
                    .filter(Student::isCaptain)
                    .findFirst()
                    .map(Student::getGamertag)
                    .orElse(null);

            List<String> memberAvatarUrls = members.stream()
                    .limit(5)
                    .map(s -> s.getAvatarUrl() != null ? s.getAvatarUrl() : "")
                    .toList();

            return TeamSummaryDto.builder()
                    .id(team.getSlug())
                    .name(team.getName())
                    .icon(team.getIcon())
                    .colour(team.getColour())
                    .hub(team.getHub())
                    .founded(team.getFounded())
                    .description(team.getDescription())
                    .game(team.getGame())
                    .memberCount(members.size())
                    .captainGamertag(captainGamertag)
                    .memberAvatarUrls(memberAvatarUrls)
                    .build();
        }).toList();
    }

    public TeamDetailDto getTeamDetail(String teamSlug) {
        Team team = teamRepository.findBySlug(teamSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        List<Student> members = studentRepository.findByTeamId(team.getId());

        // Sort: captain first, then by XP descending
        members.sort((a, b) -> {
            if (a.isCaptain() != b.isCaptain()) return a.isCaptain() ? -1 : 1;
            return Integer.compare(b.getXp(), a.getXp());
        });

        List<MainBadge> mainBadges = mainBadgeRepository.findAll();
        List<org.scottishtecharmy.wishaw_java.model.Module> modules = moduleRepository.findAll();

        List<TeamMemberDto> memberDtos = members.stream().map(s -> {
            // Badge progress for this member
            List<TeamMemberBadgeProgressDto> badgeProgress = mainBadges.stream().map(mb -> {
                MainBadgeSummaryDto summary = badgeLevelService.buildSummary(s.getId(), mb);

                return TeamMemberBadgeProgressDto.builder()
                        .mainBadgeId(summary.getId())
                        .mainBadgeName(summary.getName())
                        .mainBadgeIcon(summary.getIcon())
                        .xpEarned(summary.getXpEarned())
                        .subBadgesEarned(summary.getSubBadgesEarned())
                        .subBadgesTotal(summary.getSubBadgesTotal())
                        .levelName(summary.getLevelName())
                        .levelLabel(summary.getLevelLabel())
                        .levelColor(summary.getLevelColor())
                        .levelIcon(summary.getLevelIcon())
                        .build();
            }).toList();

            // Module progress for this member (simplified)
            List<StudentSubBadge> memberProgress = studentSubBadgeRepository.findByStudentId(s.getId());
            List<TeamMemberModuleProgressDto> moduleProgress = modules.stream().map(mod -> {
                int total = mod.getSubBadges().size();
                int completed = (int) mod.getSubBadges().stream()
                        .filter(sb -> memberProgress.stream()
                                .anyMatch(ssb -> ssb.getSubBadge().getId().equals(sb.getId()) && ssb.isEarned()))
                        .count();

                return TeamMemberModuleProgressDto.builder()
                        .moduleId(mod.getId())
                        .moduleName(mod.getName())
                        .moduleIcon(mod.getIcon())
                        .sessionsCompleted(completed)
                        .sessionsTotal(total)
                        .build();
            }).collect(java.util.stream.Collectors.toList());

            return TeamMemberDto.builder()
                    .studentId(s.getId())
                    .gamertag(s.getGamertag())
                    .realName(s.getRealName())
                    .username(s.getUsername())
                    .teamId(team.getSlug())
                    .joinedDate(s.getJoinedDate())
                    .avatarUrl(s.getAvatarUrl())
                    .isCaptain(s.isCaptain())
                    .level(s.getLevel())
                    .totalXP(s.getXp())
                    .badgeProgress(badgeProgress)
                    .moduleProgress(moduleProgress)
                    .build();
        }).toList();

        return TeamDetailDto.builder()
                .id(team.getSlug())
                .name(team.getName())
                .icon(team.getIcon())
                .colour(team.getColour())
                .hub(team.getHub())
                .founded(team.getFounded())
                .description(team.getDescription())
                .game(team.getGame())
                .members(memberDtos)
                .build();
    }
}
