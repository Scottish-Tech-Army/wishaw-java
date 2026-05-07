package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final DashboardService dashboardService;
    private final BadgeCatalogueService badgeCatalogueService;
    private final ModuleService moduleService;
    private final StudentProfileService studentProfileService;
    private final EvidenceService evidenceService;

    public StudentController(DashboardService dashboardService,
                              BadgeCatalogueService badgeCatalogueService,
                              ModuleService moduleService,
                              StudentProfileService studentProfileService,
                              EvidenceService evidenceService) {
        this.dashboardService = dashboardService;
        this.badgeCatalogueService = badgeCatalogueService;
        this.moduleService = moduleService;
        this.studentProfileService = studentProfileService;
        this.evidenceService = evidenceService;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /** GET /api/v1/students/{studentId}/dashboard */
    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<DashboardSummaryDto> getDashboard(@PathVariable Long studentId) {
        return ResponseEntity.ok(dashboardService.getDashboard(studentId));
    }

    // ── Badge Catalogue ───────────────────────────────────────────────────────

    /** GET /api/v1/students/{studentId}/badges */
    @GetMapping("/{studentId}/badges")
    public ResponseEntity<BadgeCatalogueDto> getBadgeCatalogue(@PathVariable Long studentId) {
        return ResponseEntity.ok(badgeCatalogueService.getBadgeCatalogue(studentId));
    }

    // ── Module Progress ───────────────────────────────────────────────────────

    /** GET /api/v1/students/{studentId}/modules */
    @GetMapping("/{studentId}/modules")
    public ResponseEntity<List<ModuleProgressDto>> getModuleProgress(@PathVariable Long studentId) {
        return ResponseEntity.ok(moduleService.getModuleProgress(studentId));
    }

    // ── Student Profile / Settings ────────────────────────────────────────────

    /** GET /api/v1/students/{studentId}/profile */
    @GetMapping("/{studentId}/profile")
    public ResponseEntity<StudentProfileDto> getProfile(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentProfileService.getProfile(studentId));
    }

    /** PATCH /api/v1/students/{studentId}/profile */
    @PatchMapping("/{studentId}/profile")
    public ResponseEntity<StudentProfileDto> updateProfile(
            @PathVariable Long studentId,
            @RequestBody UpdateProfileRequestDto request) {
        return ResponseEntity.ok(studentProfileService.updateProfile(studentId, request));
    }

    /** POST /api/v1/students/{studentId}/change-password */
    @PostMapping("/{studentId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long studentId,
            @RequestBody ChangePasswordRequestDto request) {
        studentProfileService.changePassword(studentId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/students/{studentId}/avatar
     *
     * Accepts a multipart/form-data body with the image file under key "file".
     * In this prototype the file is not actually stored on disk — we generate
     * a DiceBear URL as a placeholder. A real implementation would upload to
     * S3 / Azure Blob Storage.
     */
    @PostMapping("/{studentId}/avatar")
    public ResponseEntity<StudentProfileDto> uploadAvatar(
            @PathVariable Long studentId,
            @RequestParam("file") MultipartFile file) {
        // Prototype: generate a deterministic avatar URL from the student ID
        String avatarUrl = "https://api.dicebear.com/9.x/avataaars/svg?seed="
                + studentId + "&backgroundColor=b6e3f4";
        return ResponseEntity.ok(studentProfileService.uploadAvatar(studentId, avatarUrl));
    }

    // ── Evidence Submission ───────────────────────────────────────────────────

    /** GET /api/v1/students/{studentId}/evidence */
    @GetMapping("/{studentId}/evidence")
    public ResponseEntity<List<EvidenceSubmissionDto>> getEvidence(@PathVariable Long studentId) {
        return ResponseEntity.ok(evidenceService.getSubmissions(studentId));
    }

    /**
     * POST /api/v1/students/{studentId}/evidence
     *
     * Accepts multipart/form-data with fields: subBadgeId, notes, file.
     */
    @PostMapping("/{studentId}/evidence")
    public ResponseEntity<EvidenceSubmissionDto> submitEvidence(
            @PathVariable Long studentId,
            @RequestParam("subBadgeId") Long subBadgeId,
            @RequestParam("notes") String notes,
            @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload";
        return ResponseEntity.ok(evidenceService.submitEvidence(studentId, subBadgeId, notes, fileName));
    }

    // ── Public Profile Endpoints (by username) ────────────────────────────────

    /**
     * GET /api/v1/students/by-username/{username}/public-profile
     * No auth required.
     */
    @GetMapping("/by-username/{username}/public-profile")
    public ResponseEntity<PublicPlayerProfileDto> getPublicProfile(@PathVariable String username) {
        return ResponseEntity.ok(studentProfileService.getPublicProfile(username));
    }

    /**
     * GET /api/v1/students/by-username/{username}/badges/summary
     * No auth required.
     */
    @GetMapping("/by-username/{username}/badges/summary")
    public ResponseEntity<PublicBadgeSummaryDto> getPublicBadgeSummary(@PathVariable String username) {
        return ResponseEntity.ok(badgeCatalogueService.getPublicBadgeSummary(username));
    }
}
