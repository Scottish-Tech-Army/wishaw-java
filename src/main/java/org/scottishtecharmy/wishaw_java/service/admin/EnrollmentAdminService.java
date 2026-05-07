package org.scottishtecharmy.wishaw_java.service.admin;

import org.scottishtecharmy.wishaw_java.dto.request.CreateEnrollmentRequest;
import org.scottishtecharmy.wishaw_java.dto.response.EnrollmentResponse;
import org.scottishtecharmy.wishaw_java.entity.Group;
import org.scottishtecharmy.wishaw_java.entity.Module;
import org.scottishtecharmy.wishaw_java.entity.PlayerModuleEnrollment;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.EnrollmentStatus;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.DtoMapper;
import org.scottishtecharmy.wishaw_java.repository.GroupRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerModuleEnrollmentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentAdminService {

    private final PlayerModuleEnrollmentRepository enrollmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final ModuleRepository moduleRepository;
    private final GroupRepository groupRepository;

    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        UserAccount player = userAccountRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + request.getPlayerId()));
        if (player.getRole() != Role.PLAYER) {
            throw new BadRequestException("User is not a player");
        }

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + request.getModuleId()));

        PlayerModuleEnrollment enrollment = new PlayerModuleEnrollment();
        enrollment.setPlayer(player);
        enrollment.setModule(module);
        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + request.getGroupId()));
            enrollment.setGroup(group);
        }
        enrollment.setStatus(EnrollmentStatus.ASSIGNED);
        return DtoMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    public EnrollmentResponse updateStatus(Long enrollmentId, String statusValue) {
        PlayerModuleEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));
        EnrollmentStatus status;
        try {
            status = EnrollmentStatus.valueOf(statusValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid enrollment status: " + statusValue);
        }
        enrollment.setStatus(status);
        if (status == EnrollmentStatus.COMPLETED) {
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
        }
        return DtoMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }
}