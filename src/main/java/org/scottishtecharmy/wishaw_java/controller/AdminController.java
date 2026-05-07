package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.service.AdminService;
import org.scottishtecharmy.wishaw_java.service.EvidenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin portal endpoints.
 * All endpoints require ROLE_ADMIN (configured in SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final EvidenceService evidenceService;

    public AdminController(AdminService adminService, EvidenceService evidenceService) {
        this.adminService = adminService;
        this.evidenceService = evidenceService;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /** GET /api/v1/admin/dashboard */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    /** GET /api/v1/admin/activity — recent activity feed */
    @GetMapping("/activity")
    public ResponseEntity<java.util.List<AdminActivityDto>> getRecentActivities() {
        return ResponseEntity.ok(adminService.getRecentActivities());
    }

    // ── Badge Management ──────────────────────────────────────────────────────

    /** GET /api/v1/admin/badges */
    @GetMapping("/badges")
    public ResponseEntity<AdminBadgeCatalogueDto> getBadgeCatalogue() {
        return ResponseEntity.ok(adminService.getAdminBadgeCatalogue());
    }

    /** PUT /api/v1/admin/badge-levels */
    @PutMapping("/badge-levels")
    public ResponseEntity<List<BadgeLevelDto>> updateBadgeLevels(@RequestBody List<BadgeLevelDto> levels) {
        return ResponseEntity.ok(adminService.updateBadgeLevels(levels));
    }

    // ── Module Management ─────────────────────────────────────────────────────

    /** GET /api/v1/admin/modules */
    @GetMapping("/modules")
    public ResponseEntity<List<AdminModuleDto>> getAllModules() {
        return ResponseEntity.ok(adminService.getAllModules());
    }

    /** GET /api/v1/admin/modules/{moduleId} */
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<AdminModuleDto> getModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(adminService.getModule(moduleId));
    }

    /** POST /api/v1/admin/modules */
    @PostMapping("/modules")
    public ResponseEntity<AdminModuleDto> createModule(@RequestBody CreateModuleRequestDto request) {
        AdminModuleDto module = adminService.createModule(
                request.getName(),
                request.getGame(),
                request.getOutcome(),
                request.getDurationWeeks(),
                request.getStatus()
        );
        return ResponseEntity.ok(module);
    }

    /** PUT /api/v1/admin/modules/{moduleId} */
    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<AdminModuleDto> updateModule(
            @PathVariable Long moduleId,
            @RequestBody UpdateModuleRequestDto request) {
        AdminModuleDto module = adminService.updateModule(
                moduleId,
                request.getName(),
                request.getGame(),
                request.getOutcome(),
                request.getDurationWeeks(),
                request.getStatus()
        );
        return ResponseEntity.ok(module);
    }

    /** DELETE /api/v1/admin/modules/{moduleId} (archives the module) */
    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<AdminModuleDto> archiveModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(adminService.archiveModule(moduleId));
    }

    // ── Sub-Badge Management ──────────────────────────────────────────────────

    /** POST /api/v1/admin/modules/{moduleId}/sub-badges */
    @PostMapping("/modules/{moduleId}/sub-badges")
    public ResponseEntity<AdminSubBadgeDto> addSubBadge(
            @PathVariable Long moduleId,
            @RequestBody CreateSubBadgeRequestDto request) {
        AdminSubBadgeDto subBadge = adminService.addSubBadge(
                moduleId,
                request.getName(),
                request.getDescription(),
                request.getMainBadgeId(),
                request.getXpValue(),
                request.getSkills()
        );
        return ResponseEntity.ok(subBadge);
    }

    /** PUT /api/v1/admin/modules/{moduleId}/sub-badges/{subBadgeId} */
    @PutMapping("/modules/{moduleId}/sub-badges/{subBadgeId}")
    public ResponseEntity<AdminSubBadgeDto> updateSubBadge(
            @PathVariable Long moduleId,
            @PathVariable Long subBadgeId,
            @RequestBody UpdateSubBadgeRequestDto request) {
        AdminSubBadgeDto subBadge = adminService.updateSubBadge(
                moduleId,
                subBadgeId,
                request.getName(),
                request.getDescription(),
                request.getMainBadgeId(),
                request.getXpValue(),
                request.getSkills()
        );
        return ResponseEntity.ok(subBadge);
    }

    /** DELETE /api/v1/admin/modules/{moduleId}/sub-badges/{subBadgeId} */
    @DeleteMapping("/modules/{moduleId}/sub-badges/{subBadgeId}")
    public ResponseEntity<Void> removeSubBadge(
            @PathVariable Long moduleId,
            @PathVariable Long subBadgeId) {
        adminService.removeSubBadge(moduleId, subBadgeId);
        return ResponseEntity.noContent().build();
    }

    /** PUT /api/v1/admin/modules/{moduleId}/sub-badges/reorder */
    @PutMapping("/modules/{moduleId}/sub-badges/reorder")
    public ResponseEntity<List<AdminSubBadgeDto>> reorderSubBadges(
            @PathVariable Long moduleId,
            @RequestBody ReorderSubBadgesRequestDto request) {
        return ResponseEntity.ok(adminService.reorderSubBadges(moduleId, request.getOrderedIds()));
    }

    // ── Session Management ────────────────────────────────────────────────────

    /** POST /api/v1/admin/modules/{moduleId}/sessions */
    @PostMapping("/modules/{moduleId}/sessions")
    public ResponseEntity<AdminSessionDto> addSession(
            @PathVariable Long moduleId,
            @RequestBody CreateSessionRequestDto request) {
        AdminSessionDto session = adminService.addSession(
                moduleId,
                request.getWeekNumber(),
                request.getTitle(),
                request.getSessionPlan(),
                request.getDeliveryNotes()
        );
        return ResponseEntity.ok(session);
    }

    /** PUT /api/v1/admin/modules/{moduleId}/sessions/{sessionId} */
    @PutMapping("/modules/{moduleId}/sessions/{sessionId}")
    public ResponseEntity<AdminSessionDto> updateSession(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId,
            @RequestBody UpdateSessionRequestDto request) {
        AdminSessionDto session = adminService.updateSession(
                moduleId,
                sessionId,
                request.getWeekNumber(),
                request.getTitle(),
                request.getSessionPlan(),
                request.getDeliveryNotes()
        );
        return ResponseEntity.ok(session);
    }

    /** DELETE /api/v1/admin/modules/{moduleId}/sessions/{sessionId} */
    @DeleteMapping("/modules/{moduleId}/sessions/{sessionId}")
    public ResponseEntity<Void> removeSession(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId) {
        adminService.removeSession(moduleId, sessionId);
        return ResponseEntity.noContent().build();
    }

    // ── Resource Management ───────────────────────────────────────────────────

    /**
     * POST /api/v1/admin/modules/{moduleId}/sessions/{sessionId}/resources
     *
     * Accepts a multipart/form-data body with the file under key "file".
     * In this prototype the file is not actually stored — we generate a mock URL.
     */
    @PostMapping("/modules/{moduleId}/sessions/{sessionId}/resources")
    public ResponseEntity<AdminResourceDto> uploadResource(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload";
        String fileType = inferFileType(fileName);
        long fileSizeBytes = file.getSize();
        String url = "/mock/files/" + fileName;  // Prototype — would be S3 URL in production

        return ResponseEntity.ok(adminService.uploadResource(moduleId, sessionId, fileName, fileType, fileSizeBytes, url));
    }

    /** DELETE /api/v1/admin/modules/{moduleId}/sessions/{sessionId}/resources/{resourceId} */
    @DeleteMapping("/modules/{moduleId}/sessions/{sessionId}/resources/{resourceId}")
    public ResponseEntity<Void> removeResource(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId,
            @PathVariable Long resourceId) {
        adminService.removeResource(moduleId, sessionId, resourceId);
        return ResponseEntity.noContent().build();
    }

    /** Infer file type from extension */
    private String inferFileType(String fileName) {
        if (fileName == null) return "other";
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pptx") || lower.endsWith(".ppt")) return "pptx";
        if (lower.endsWith(".pdf")) return "pdf";
        if (lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".webm")) return "video";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif")) return "image";
        if (lower.endsWith(".doc") || lower.endsWith(".docx")) return "doc";
        return "other";
    }

    // ── User Management ───────────────────────────────────────────────────────

    /** GET /api/v1/admin/users */
    @GetMapping("/users")
    public ResponseEntity<java.util.List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /** GET /api/v1/admin/users/{studentId} */
    @GetMapping("/users/{studentId}")
    public ResponseEntity<AdminUserDto> getUser(@PathVariable Long studentId) {
        return ResponseEntity.ok(adminService.getUser(studentId));
    }

    /** POST /api/v1/admin/users */
    @PostMapping("/users")
    public ResponseEntity<AdminUserDto> createUser(@RequestBody CreateUserRequestDto request) {
        AdminUserDto user = adminService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getName(),
                request.getGamertag(),
                request.getCentre(),
                request.getGroup()
        );
        return ResponseEntity.ok(user);
    }

    /** POST /api/v1/admin/users/{studentId}/award-xp — directly award XP */
    @PostMapping("/users/{studentId}/award-xp")
    public ResponseEntity<AdminUserDto> awardXp(
            @PathVariable Long studentId,
            @RequestBody AwardXpRequestDto request) {
        return ResponseEntity.ok(adminService.awardXp(studentId, request.getXp(), request.getReason()));
    }

    /** POST /api/v1/admin/users/{studentId}/award-badge/{subBadgeId} — directly award a sub-badge */
    @PostMapping("/users/{studentId}/award-badge/{subBadgeId}")
    public ResponseEntity<AdminUserDto> awardSubBadge(
            @PathVariable Long studentId,
            @PathVariable Long subBadgeId) {
        return ResponseEntity.ok(adminService.awardSubBadge(studentId, subBadgeId));
    }

    // ── Evidence Review ───────────────────────────────────────────────────────

    /** GET /api/v1/admin/evidence — all pending evidence submissions */
    @GetMapping("/evidence")
    public ResponseEntity<List<EvidenceSubmissionDto>> getPendingEvidence() {
        return ResponseEntity.ok(evidenceService.getAllPendingSubmissions());
    }

    /** GET /api/v1/admin/evidence/all — all evidence submissions */
    @GetMapping("/evidence/all")
    public ResponseEntity<List<EvidenceSubmissionDto>> getAllEvidence() {
        return ResponseEntity.ok(evidenceService.getAllSubmissions());
    }

    /** POST /api/v1/admin/evidence/{submissionId}/approve */
    @PostMapping("/evidence/{submissionId}/approve")
    public ResponseEntity<EvidenceSubmissionDto> approveEvidence(@PathVariable Long submissionId) {
        return ResponseEntity.ok(evidenceService.approveEvidence(submissionId));
    }

    /** POST /api/v1/admin/evidence/{submissionId}/reject */
    @PostMapping("/evidence/{submissionId}/reject")
    public ResponseEntity<EvidenceSubmissionDto> rejectEvidence(@PathVariable Long submissionId) {
        return ResponseEntity.ok(evidenceService.rejectEvidence(submissionId));
    }
}
