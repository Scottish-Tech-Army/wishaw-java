package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LeaderboardService {

    private final StudentRepository studentRepository;
    private final CentreRepository centreRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;
    private final ModuleRepository moduleRepository;

    public LeaderboardService(StudentRepository studentRepository,
                              CentreRepository centreRepository,
                              StudentSubBadgeRepository studentSubBadgeRepository,
                              ModuleRepository moduleRepository) {
        this.studentRepository = studentRepository;
        this.centreRepository = centreRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
        this.moduleRepository = moduleRepository;
    }

    public LeaderboardResponseDto getLeaderboard(String period, String sortBy,
                                                  int page, int size,
                                                  String currentUsername) {
        List<Student> allStudents = studentRepository.findAllByOrderByXpDesc();

        // Build player rows
        List<LeaderboardPlayerDto> playerRows = new ArrayList<>();
        for (int i = 0; i < allStudents.size(); i++) {
            Student s = allStudents.get(i);
            long earnedBadges = studentSubBadgeRepository.countByStudentIdAndEarnedTrue(s.getId());
            playerRows.add(LeaderboardPlayerDto.builder()
                    .rank(i + 1)
                    .studentId(s.getId())
                    .name(s.getRealName())
                    .username(s.getUsername())
                    .gamertag(s.getGamertag())
                    .level(s.getLevel())
                    .periodXp(s.getXp())
                    .completedModules(0) // simplified
                    .badgesCompleted((int) earnedBadges)
                    .centre(s.getCentre() != null ? s.getCentre().getName() : "")
                    .avatarUrl(s.getAvatarUrl())
                    .badgeIcons(List.of())
                    .build());
        }

        // Sort by requested key
        if ("LEVEL".equals(sortBy)) {
            playerRows.sort(Comparator.comparingInt(LeaderboardPlayerDto::getLevel).reversed());
        } else if ("BADGES".equals(sortBy)) {
            playerRows.sort(Comparator.comparingInt(LeaderboardPlayerDto::getBadgesCompleted).reversed());
        }
        // Re-assign ranks after sorting
        for (int i = 0; i < playerRows.size(); i++) {
            playerRows.get(i).setRank(i + 1);
        }

        int totalCount = playerRows.size();

        // Paginate
        int start = page * size;
        int end = Math.min(start + size, playerRows.size());
        if (start >= playerRows.size()) {
            playerRows = List.of();
        } else {
            playerRows = playerRows.subList(start, end);
        }

        // Build centre rows
        List<Centre> centres = centreRepository.findAll();
        List<LeaderboardCentreDto> centreRows = new ArrayList<>();
        for (int i = 0; i < centres.size(); i++) {
            Centre c = centres.get(i);
            List<Student> members = studentRepository.findByCentreId(c.getId());
            int totalXp = members.stream().mapToInt(Student::getXp).sum();
            int totalBadges = members.stream()
                    .mapToInt(s -> (int) studentSubBadgeRepository.countByStudentIdAndEarnedTrue(s.getId()))
                    .sum();
            String topPlayer = members.stream()
                    .max(Comparator.comparingInt(Student::getXp))
                    .map(Student::getRealName)
                    .orElse("");

            centreRows.add(LeaderboardCentreDto.builder()
                    .rank(i + 1)
                    .name(c.getName())
                    .icon(c.getIcon())
                    .memberCount(members.size())
                    .periodXp(totalXp)
                    .totalBadges(totalBadges)
                    .totalModules(0)
                    .topPlayerName(topPlayer)
                    .build());
        }
        // Sort centres by XP and re-rank
        centreRows.sort(Comparator.comparingInt(LeaderboardCentreDto::getPeriodXp).reversed());
        for (int i = 0; i < centreRows.size(); i++) {
            centreRows.get(i).setRank(i + 1);
        }

        // Resolve current user's centre
        String currentUserCentreName = null;
        if (currentUsername != null) {
            currentUserCentreName = studentRepository.findByUsername(currentUsername)
                    .map(s -> s.getCentre() != null ? s.getCentre().getName() : null)
                    .orElse(null);
        }

        return LeaderboardResponseDto.builder()
                .period(period)
                .players(playerRows)
                .centres(centreRows)
                .totalCount(totalCount)
                .currentUserUsername(currentUsername)
                .currentUserCentreName(currentUserCentreName)
                .build();
    }
}
