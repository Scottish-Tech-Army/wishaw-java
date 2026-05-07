package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModuleRepository moduleRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;

    public StudentProfileService(StudentRepository studentRepository,
                                  PasswordEncoder passwordEncoder,
                                  ModuleRepository moduleRepository,
                                  StudentSubBadgeRepository studentSubBadgeRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.moduleRepository = moduleRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
    }

    public StudentProfileDto getProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        return StudentProfileDto.builder()
                .studentId(student.getId())
                .username(student.getUsername())
                .gamertag(student.getGamertag())
                .bio(student.getBio())
                .avatarUrl(student.getAvatarUrl())
                .build();
    }

    public StudentProfileDto updateProfile(Long studentId, UpdateProfileRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (request.getUsername() != null) {
            student.setUsername(request.getUsername().trim());
        }
        if (request.getGamertag() != null) {
            student.setGamertag(request.getGamertag().trim());
        }
        if (request.getBio() != null) {
            student.setBio(request.getBio().trim());
        }

        studentRepository.save(student);

        return StudentProfileDto.builder()
                .studentId(student.getId())
                .username(student.getUsername())
                .gamertag(student.getGamertag())
                .bio(student.getBio())
                .avatarUrl(student.getAvatarUrl())
                .build();
    }

    public void changePassword(Long studentId, ChangePasswordRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), student.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect.");
        }

        student.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        studentRepository.save(student);
    }

    public StudentProfileDto uploadAvatar(Long studentId, String avatarUrl) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        student.setAvatarUrl(avatarUrl);
        studentRepository.save(student);

        return StudentProfileDto.builder()
                .studentId(student.getId())
                .username(student.getUsername())
                .gamertag(student.getGamertag())
                .bio(student.getBio())
                .avatarUrl(student.getAvatarUrl())
                .build();
    }

    public PublicPlayerProfileDto getPublicProfile(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // Compute global rank
        var allStudents = studentRepository.findAllByOrderByXpDesc();
        Integer globalRank = null;
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(student.getId())) {
                globalRank = i + 1;
                break;
            }
        }

        Team team = student.getTeam();

        // Build module progress
        List<StudentSubBadge> studentProgress = studentSubBadgeRepository.findByStudentId(student.getId());
        List<org.scottishtecharmy.wishaw_java.model.Module> modules = moduleRepository.findAll();
        List<PublicModuleProgressDto> modProgress = modules.stream().map(mod -> {
            int total = mod.getSubBadges().size();
            int completed = (int) mod.getSubBadges().stream()
                    .filter(sb -> studentProgress.stream()
                            .anyMatch(ssb -> ssb.getSubBadge().getId().equals(sb.getId()) && ssb.isEarned()))
                    .count();
            return PublicModuleProgressDto.builder()
                    .moduleId(mod.getId())
                    .moduleName(mod.getName())
                    .moduleIcon(mod.getIcon())
                    .sessionsCompleted(completed)
                    .sessionsTotal(total)
                    .build();
        }).toList();

        return PublicPlayerProfileDto.builder()
                .username(student.getUsername())
                .gamertag(student.getGamertag())
                .realName(student.getRealName())
                .bio(student.getBio())
                .joinedDate(student.getJoinedDate())
                .level(student.getLevel())
                .totalXP(student.getXp())
                .avatarUrl(student.getAvatarUrl())
                .teamName(team != null ? team.getName() : null)
                .teamIcon(team != null ? team.getIcon() : null)
                .teamId(team != null ? team.getSlug() : null)
                .teamColour(team != null ? team.getColour() : null)
                .hub(student.getCentre() != null ? student.getCentre().getName() : null)
                .isCaptain(student.isCaptain())
                .globalRank(globalRank)
                .moduleProgress(modProgress)
                .build();
    }
}
