package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.EvidenceSubmissionDto;
import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EvidenceService {

    private static final int XP_FOR_SUBMISSION = 10;

    private final EvidenceSubmissionRepository evidenceSubmissionRepository;
    private final StudentRepository studentRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;
    private final XpService xpService;

    public EvidenceService(EvidenceSubmissionRepository evidenceSubmissionRepository,
                           StudentRepository studentRepository,
                           SubBadgeRepository subBadgeRepository,
                           StudentSubBadgeRepository studentSubBadgeRepository,
                           XpService xpService) {
        this.evidenceSubmissionRepository = evidenceSubmissionRepository;
        this.studentRepository = studentRepository;
        this.subBadgeRepository = subBadgeRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
        this.xpService = xpService;
    }

    public List<EvidenceSubmissionDto> getSubmissions(Long studentId) {
        return evidenceSubmissionRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** Get all pending evidence submissions (for admin review). */
    public List<EvidenceSubmissionDto> getAllPendingSubmissions() {
        return evidenceSubmissionRepository.findByStatusOrderBySubmittedAtDesc("pending")
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** Get all evidence submissions regardless of status (for admin). */
    public List<EvidenceSubmissionDto> getAllSubmissions() {
        return evidenceSubmissionRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EvidenceSubmissionDto submitEvidence(Long studentId, Long subBadgeId, String notes, String fileName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        SubBadge subBadge = subBadgeRepository.findById(subBadgeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub-badge not found"));

        EvidenceSubmission submission = EvidenceSubmission.builder()
                .student(student)
                .subBadge(subBadge)
                .notes(notes)
                .fileName(fileName)
                .submittedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")))
                .status("pending")
                .build();

        submission = evidenceSubmissionRepository.save(submission);

        // Award a small amount of XP for submitting evidence
        xpService.awardXp(student, XP_FOR_SUBMISSION,
                "Evidence submitted for " + subBadge.getName(), "📝");

        return toDto(submission);
    }

    /**
     * Approve an evidence submission.
     * Awards the sub-badge's XP reward, marks the student's sub-badge as earned,
     * and records activity.
     */
    @Transactional
    public EvidenceSubmissionDto approveEvidence(Long submissionId) {
        EvidenceSubmission submission = evidenceSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (!"pending".equals(submission.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Submission has already been " + submission.getStatus());
        }

        submission.setStatus("approved");
        evidenceSubmissionRepository.save(submission);

        Student student = submission.getStudent();
        SubBadge subBadge = submission.getSubBadge();

        // Mark the sub-badge as earned
        StudentSubBadge progress = studentSubBadgeRepository
                .findByStudentIdAndSubBadgeId(student.getId(), subBadge.getId())
                .orElseGet(() -> StudentSubBadge.builder()
                        .student(student)
                        .subBadge(subBadge)
                        .earned(false)
                        .build());

        if (!progress.isEarned()) {
            progress.setEarned(true);
            progress.setEarnedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM yyyy")));
            studentSubBadgeRepository.save(progress);

            // Award the sub-badge's XP reward
            xpService.awardXp(student, subBadge.getXpReward(),
                    "Earned sub-badge: " + subBadge.getName(), "🏅");
        }

        return toDto(submission);
    }

    /**
     * Reject an evidence submission. No XP is awarded.
     */
    @Transactional
    public EvidenceSubmissionDto rejectEvidence(Long submissionId) {
        EvidenceSubmission submission = evidenceSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (!"pending".equals(submission.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Submission has already been " + submission.getStatus());
        }

        submission.setStatus("rejected");
        evidenceSubmissionRepository.save(submission);

        return toDto(submission);
    }

    private EvidenceSubmissionDto toDto(EvidenceSubmission es) {
        SubBadge sb = es.getSubBadge();
        String badgeName = sb.getMainBadge() != null ? sb.getMainBadge().getName() : "";
        Student stu = es.getStudent();

        return EvidenceSubmissionDto.builder()
                .id(es.getId())
                .studentId(stu.getId())
                .studentUsername(stu.getUsername())
                .studentName(stu.getRealName())
                .badgeName(badgeName)
                .subBadgeName(sb.getName())
                .subBadgeIcon(sb.getIcon())
                .fileName(es.getFileName())
                .notes(es.getNotes())
                .submittedAt(es.getSubmittedAt())
                .status(es.getStatus())
                .build();
    }
}
