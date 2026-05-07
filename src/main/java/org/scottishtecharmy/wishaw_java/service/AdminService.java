package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.model.Module;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for admin portal operations.
 */
@Service
public class AdminService {

    private final BadgeLevelRepository badgeLevelRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final SessionRepository sessionRepository;
    private final ResourceRepository resourceRepository;
    private final BadgeLevelService badgeLevelService;
    private final CentreRepository centreRepository;
    private final XpEventRepository xpEventRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final XpService xpService;

    public AdminService(BadgeLevelRepository badgeLevelRepository,
                        MainBadgeRepository mainBadgeRepository,
                        SubBadgeRepository subBadgeRepository,
                        StudentSubBadgeRepository studentSubBadgeRepository,
                        StudentRepository studentRepository,
                        ModuleRepository moduleRepository,
                        SessionRepository sessionRepository,
                        ResourceRepository resourceRepository,
                        BadgeLevelService badgeLevelService,
                        CentreRepository centreRepository,
                        XpEventRepository xpEventRepository,
                        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                        XpService xpService) {
        this.badgeLevelRepository = badgeLevelRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.subBadgeRepository = subBadgeRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
        this.studentRepository = studentRepository;
        this.moduleRepository = moduleRepository;
        this.sessionRepository = sessionRepository;
        this.resourceRepository = resourceRepository;
        this.badgeLevelService = badgeLevelService;
        this.centreRepository = centreRepository;
        this.xpEventRepository = xpEventRepository;
        this.passwordEncoder = passwordEncoder;
        this.xpService = xpService;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /**
     * Build admin dashboard data: stats + recent activity feed.
     */
    public AdminDashboardDto getDashboard() {
        // Count users (exclude admins)
        long totalUsers = studentRepository.count() - studentRepository.countByRole("ROLE_ADMIN");
        
        // Count active modules (status = "Active")
        long activeModules = moduleRepository.findAll().stream()
                .filter(m -> "Active".equalsIgnoreCase(m.getStatus()))
                .count();
        
        // Count centres
        long centreCount = centreRepository.count();
        
        // Count badges awarded this week (XP events with icon "🏅" in last 7 days)
        String weekAgo = java.time.LocalDate.now().minusDays(7)
                .format(java.time.format.DateTimeFormatter.ISO_DATE);
        long badgesThisWeek = xpEventRepository.countByDateGreaterThanEqual(weekAgo);
        
        // Build recent activity from XP events
        List<XpEvent> recentXpEvents = xpEventRepository.findTop20ByOrderByDateDescIdDesc();
        List<AdminActivityDto> activities = recentXpEvents.stream()
                .map(this::toAdminActivity)
                .toList();
        
        return AdminDashboardDto.builder()
                .totalUsers((int) totalUsers)
                .activeGroups(0) // Groups not yet implemented
                .modulesInProgress((int) activeModules)
                .badgesAwardedThisWeek((int) badgesThisWeek)
                .centreCount((int) centreCount)
                .recentActivity(activities)
                .build();
    }
    
    private AdminActivityDto toAdminActivity(XpEvent event) {
        String type = "xp";
        if (event.getIcon() != null && event.getIcon().contains("🏅")) {
            type = "badge";
        } else if (event.getActivity() != null && event.getActivity().toLowerCase().contains("module")) {
            type = "module";
        }
        
        String centre = event.getStudent().getCentre() != null 
                ? event.getStudent().getCentre().getName() 
                : "Unknown";
        
        return AdminActivityDto.builder()
                .id(event.getId())
                .type(type)
                .icon(event.getIcon())
                .action(event.getActivity() + " — " + event.getStudent().getGamertag())
                .centre(centre)
                .admin("System")
                .time(formatRelativeTime(event.getDate()))
                .build();
    }
    
    private String formatRelativeTime(String isoDate) {
        if (isoDate == null) return "Unknown";
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(isoDate);
            java.time.LocalDate today = java.time.LocalDate.now();
            long days = java.time.temporal.ChronoUnit.DAYS.between(date, today);
            if (days == 0) return "Today";
            if (days == 1) return "Yesterday";
            if (days < 7) return days + " days ago";
            if (days < 30) return (days / 7) + " weeks ago";
            return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"));
        } catch (Exception e) {
            return isoDate;
        }
    }

    /**
     * Get recent activities for the admin activity feed.
     */
    public List<AdminActivityDto> getRecentActivities() {
        List<XpEvent> recentXpEvents = xpEventRepository.findTop20ByOrderByDateDescIdDesc();
        return recentXpEvents.stream()
                .map(this::toAdminActivity)
                .toList();
    }

    // ── Badge Management ──────────────────────────────────────────────────────

    public AdminBadgeCatalogueDto getAdminBadgeCatalogue() {
        List<BadgeLevelDto> badgeLevels = badgeLevelService.getAllBadgeLevels();
        List<MainBadge> mainBadges = mainBadgeRepository.findAll();

        // Build badge details without student-specific progress
        List<MainBadgeDetailDto> badges = mainBadges.stream()
                .map(this::toMainBadgeDetail)
                .toList();

        // Build per-badge leaderboards
        Map<String, List<BadgeLeaderboardEntryDto>> leaderboards = new HashMap<>();
        for (MainBadge mb : mainBadges) {
            leaderboards.put(mb.getSlug(), buildBadgeLeaderboard(mb));
        }

        return AdminBadgeCatalogueDto.builder()
                .badgeLevels(badgeLevels)
                .badges(badges)
                .badgeLeaderboards(leaderboards)
                .build();
    }

    private MainBadgeDetailDto toMainBadgeDetail(MainBadge mb) {
        List<SubBadgeDetailDto> subBadges = mb.getSubBadges().stream()
                .map(sb -> SubBadgeDetailDto.builder()
                        .id(sb.getId())
                        .icon(sb.getIcon())
                        .name(sb.getName())
                        .shortDesc(sb.getShortDesc())
                        .criteria(sb.getCriteria())
                        .xpReward(sb.getXpReward())
                        .type(sb.getType())
                        .skills(sb.getSkills() != null
                                ? Arrays.asList(sb.getSkills().split(",\\s*"))
                                : List.of())
                        .earned(false)
                        .earnedDate(null)
                        .build())
                .toList();

        return MainBadgeDetailDto.builder()
                .id(mb.getSlug())
                .icon(mb.getIcon())
                .name(mb.getName())
                .tagline(mb.getTagline())
                .description(mb.getDescription())
                .xpEarned(0)
                .subBadges(subBadges)
                .build();
    }

    private List<BadgeLeaderboardEntryDto> buildBadgeLeaderboard(MainBadge mainBadge) {
        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> !"ROLE_ADMIN".equals(s.getRole()))
                .toList();

        List<BadgeLeaderboardEntryDto> entries = new ArrayList<>();
        for (Student student : students) {
            int xp = studentSubBadgeRepository.findByStudentIdAndSubBadgeMainBadgeId(student.getId(), mainBadge.getId())
                    .stream()
                    .filter(StudentSubBadge::isEarned)
                    .mapToInt(ssb -> ssb.getSubBadge().getXpReward())
                    .sum();
            if (xp > 0) {
                entries.add(BadgeLeaderboardEntryDto.builder()
                        .rank(0)
                        .name(student.getRealName())
                        .username(student.getUsername())
                        .xp(xp)
                        .build());
            }
        }

        // Sort by XP descending and assign ranks
        entries.sort(Comparator.comparingInt(BadgeLeaderboardEntryDto::getXp).reversed());
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        // Return top 5
        return entries.stream().limit(5).toList();
    }

    @Transactional
    public List<BadgeLevelDto> updateBadgeLevels(List<BadgeLevelDto> levels) {
        // Delete all existing badge levels and recreate
        badgeLevelRepository.deleteAll();

        List<BadgeLevel> newLevels = new ArrayList<>();
        for (int i = 0; i < levels.size(); i++) {
            BadgeLevelDto dto = levels.get(i);
            BadgeLevel entity = BadgeLevel.builder()
                    .name(dto.getName())
                    .label(dto.getLabel())
                    .minXP(dto.getMinXP())
                    .maxXP(dto.getMaxXP())
                    .color(dto.getColor())
                    .icon(dto.getIcon())
                    .sortOrder(i + 1)
                    .build();
            newLevels.add(badgeLevelRepository.save(entity));
        }

        return newLevels.stream().map(badgeLevelService::toDto).toList();
    }

    // ── Module Management ─────────────────────────────────────────────────────

    public List<AdminModuleDto> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::toAdminModuleDto)
                .toList();
    }

    public AdminModuleDto getModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        return toAdminModuleDto(module);
    }

    @Transactional
    public AdminModuleDto createModule(String name, String game, String outcome, int durationWeeks, String status) {
        Module module = Module.builder()
                .name(name)
                .game(game)
                .outcome(outcome)
                .durationWeeks(durationWeeks)
                .status(status != null ? status : "Draft")
                .subBadges(new ArrayList<>())
                .sessions(new ArrayList<>())
                .build();
        module = moduleRepository.save(module);
        return toAdminModuleDto(module);
    }

    @Transactional
    public AdminModuleDto updateModule(Long moduleId, String name, String game, String outcome,
                                       Integer durationWeeks, String status) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        if (name != null) module.setName(name);
        if (game != null) module.setGame(game);
        if (outcome != null) module.setOutcome(outcome);
        if (durationWeeks != null) module.setDurationWeeks(durationWeeks);
        if (status != null) module.setStatus(status);

        module = moduleRepository.save(module);
        return toAdminModuleDto(module);
    }

    @Transactional
    public AdminModuleDto archiveModule(Long moduleId) {
        return updateModule(moduleId, null, null, null, null, "Archived");
    }

    private AdminModuleDto toAdminModuleDto(Module module) {
        List<AdminSubBadgeDto> subBadges = module.getSubBadges().stream()
                .map(sb -> AdminSubBadgeDto.builder()
                        .id(sb.getId())
                        .name(sb.getName())
                        .description(sb.getShortDesc())
                        .mainBadgeId(sb.getMainBadge().getSlug())
                        .xpValue(sb.getXpReward())
                        .skills(sb.getSkills() != null
                                ? Arrays.asList(sb.getSkills().split(",\\s*"))
                                : List.of())
                        .build())
                .toList();

        List<AdminSessionDto> sessions = module.getSessions().stream()
                .map(this::toAdminSessionDto)
                .toList();

        // Groups using this module — simplified: just return empty for now
        List<String> groupsUsingIt = List.of();

        return AdminModuleDto.builder()
                .id(module.getId())
                .name(module.getName())
                .game(module.getGame())
                .outcome(module.getOutcome())
                .durationWeeks(module.getDurationWeeks())
                .status(module.getStatus())
                .subBadges(subBadges)
                .groupsUsingIt(groupsUsingIt)
                .sessions(sessions)
                .build();
    }

    private AdminSessionDto toAdminSessionDto(Session session) {
        List<AdminResourceDto> resources = session.getResources().stream()
                .map(r -> AdminResourceDto.builder()
                        .id(r.getId())
                        .fileName(r.getFileName())
                        .fileType(r.getFileType())
                        .fileSizeBytes(r.getFileSizeBytes())
                        .url(r.getUrl())
                        .uploadedAt(r.getUploadedAt())
                        .build())
                .toList();

        return AdminSessionDto.builder()
                .id(session.getId())
                .weekNumber(session.getWeekNumber())
                .title(session.getTitle())
                .sessionPlan(session.getSessionPlan())
                .deliveryNotes(session.getDeliveryNotes())
                .resources(resources)
                .build();
    }

    // ── Sub-Badge Management ──────────────────────────────────────────────────

    @Transactional
    public AdminSubBadgeDto addSubBadge(Long moduleId, String name, String description,
                                        String mainBadgeId, int xpValue, List<String> skills) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        MainBadge mainBadge = mainBadgeRepository.findBySlug(mainBadgeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Main badge not found"));

        SubBadge subBadge = SubBadge.builder()
                .name(name)
                .shortDesc(description)
                .criteria(description)
                .mainBadge(mainBadge)
                .xpReward(xpValue)
                .skills(skills != null ? String.join(", ", skills) : "")
                .type("activity")
                .icon("🎯")
                .build();
        subBadge = subBadgeRepository.save(subBadge);

        module.getSubBadges().add(subBadge);
        moduleRepository.save(module);

        return AdminSubBadgeDto.builder()
                .id(subBadge.getId())
                .name(subBadge.getName())
                .description(subBadge.getShortDesc())
                .mainBadgeId(mainBadge.getSlug())
                .xpValue(subBadge.getXpReward())
                .skills(skills != null ? skills : List.of())
                .build();
    }

    @Transactional
    public AdminSubBadgeDto updateSubBadge(Long moduleId, Long subBadgeId, String name,
                                           String description, String mainBadgeId,
                                           Integer xpValue, List<String> skills) {
        SubBadge subBadge = subBadgeRepository.findById(subBadgeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub-badge not found"));

        if (name != null) subBadge.setName(name);
        if (description != null) {
            subBadge.setShortDesc(description);
            subBadge.setCriteria(description);
        }
        if (mainBadgeId != null) {
            MainBadge mainBadge = mainBadgeRepository.findBySlug(mainBadgeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Main badge not found"));
            subBadge.setMainBadge(mainBadge);
        }
        if (xpValue != null) subBadge.setXpReward(xpValue);
        if (skills != null) subBadge.setSkills(String.join(", ", skills));

        subBadge = subBadgeRepository.save(subBadge);

        return AdminSubBadgeDto.builder()
                .id(subBadge.getId())
                .name(subBadge.getName())
                .description(subBadge.getShortDesc())
                .mainBadgeId(subBadge.getMainBadge().getSlug())
                .xpValue(subBadge.getXpReward())
                .skills(subBadge.getSkills() != null
                        ? Arrays.asList(subBadge.getSkills().split(",\\s*"))
                        : List.of())
                .build();
    }

    @Transactional
    public void removeSubBadge(Long moduleId, Long subBadgeId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        module.getSubBadges().removeIf(sb -> sb.getId().equals(subBadgeId));
        moduleRepository.save(module);
    }

    @Transactional
    public List<AdminSubBadgeDto> reorderSubBadges(Long moduleId, List<Long> orderedIds) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        Map<Long, SubBadge> byId = module.getSubBadges().stream()
                .collect(Collectors.toMap(SubBadge::getId, sb -> sb));

        List<SubBadge> reordered = orderedIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .toList();

        module.setSubBadges(new ArrayList<>(reordered));
        moduleRepository.save(module);

        return reordered.stream()
                .map(sb -> AdminSubBadgeDto.builder()
                        .id(sb.getId())
                        .name(sb.getName())
                        .description(sb.getShortDesc())
                        .mainBadgeId(sb.getMainBadge().getSlug())
                        .xpValue(sb.getXpReward())
                        .skills(sb.getSkills() != null
                                ? Arrays.asList(sb.getSkills().split(",\\s*"))
                                : List.of())
                        .build())
                .toList();
    }

    // ── Session Management ────────────────────────────────────────────────────

    @Transactional
    public AdminSessionDto addSession(Long moduleId, int weekNumber, String title,
                                      String sessionPlan, String deliveryNotes) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        Session session = Session.builder()
                .module(module)
                .weekNumber(weekNumber)
                .title(title)
                .sessionPlan(sessionPlan)
                .deliveryNotes(deliveryNotes)
                .resources(new ArrayList<>())
                .build();
        session = sessionRepository.save(session);

        return toAdminSessionDto(session);
    }

    @Transactional
    public AdminSessionDto updateSession(Long moduleId, Long sessionId, Integer weekNumber,
                                         String title, String sessionPlan, String deliveryNotes) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        if (weekNumber != null) session.setWeekNumber(weekNumber);
        if (title != null) session.setTitle(title);
        if (sessionPlan != null) session.setSessionPlan(sessionPlan);
        if (deliveryNotes != null) session.setDeliveryNotes(deliveryNotes);

        session = sessionRepository.save(session);
        return toAdminSessionDto(session);
    }

    @Transactional
    public void removeSession(Long moduleId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        sessionRepository.delete(session);
    }

    // ── Resource Management ───────────────────────────────────────────────────

    @Transactional
    public AdminResourceDto uploadResource(Long moduleId, Long sessionId, String fileName,
                                           String fileType, long fileSizeBytes, String url) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        Resource resource = Resource.builder()
                .session(session)
                .fileName(fileName)
                .fileType(fileType)
                .fileSizeBytes(fileSizeBytes)
                .url(url)
                .uploadedAt(java.time.Instant.now().toString())
                .build();
        resource = resourceRepository.save(resource);

        return AdminResourceDto.builder()
                .id(resource.getId())
                .fileName(resource.getFileName())
                .fileType(resource.getFileType())
                .fileSizeBytes(resource.getFileSizeBytes())
                .url(resource.getUrl())
                .uploadedAt(resource.getUploadedAt())
                .build();
    }

    @Transactional
    public void removeResource(Long moduleId, Long sessionId, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        resourceRepository.delete(resource);
    }

    // ── User Management ───────────────────────────────────────────────────────

    /**
     * Create a new student user.
     */
    @Transactional
    public AdminUserDto createUser(String username, String password, String name,
                                   String gamertag, String centreName, String group) {
        // Check if username already exists
        if (studentRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // Find or create centre
        Centre centre = centreRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(centreName))
                .findFirst()
                .orElse(null);

        String joinedDate = java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("MMM yyyy"));

        Student student = Student.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .passwordHint("Set by admin")
                .gamertag(gamertag != null ? gamertag : name)
                .realName(name)
                .role("ROLE_STUDENT")
                .level(1)
                .xp(0)
                .joinedDate(joinedDate)
                .centre(centre)
                .build();

        student = studentRepository.save(student);

        // Initialize sub-badge progress for the new student
        List<SubBadge> allSubBadges = subBadgeRepository.findAll();
        for (SubBadge sb : allSubBadges) {
            studentSubBadgeRepository.save(StudentSubBadge.builder()
                    .student(student)
                    .subBadge(sb)
                    .earned(false)
                    .earnedDate(null)
                    .build());
        }

        return toAdminUserDto(student);
    }

    /**
     * List all student users.
     */
    public List<AdminUserDto> getAllUsers() {
        return studentRepository.findAll().stream()
                .filter(s -> !"ROLE_ADMIN".equals(s.getRole()))
                .map(this::toAdminUserDto)
                .toList();
    }

    private AdminUserDto toAdminUserDto(Student student) {
        int badgesEarned = (int) studentSubBadgeRepository.countByStudentIdAndEarnedTrue(student.getId());

        return AdminUserDto.builder()
                .id(student.getId())
                .username(student.getUsername())
                .name(student.getRealName())
                .gamertag(student.getGamertag())
                .centre(student.getCentre() != null ? student.getCentre().getName() : null)
                .group(null) // Groups not implemented yet
                .level(student.getLevel())
                .totalXP(student.getXp())
                .badgesEarned(badgesEarned)
                .joinedDate(student.getJoinedDate())
                .avatarUrl(student.getAvatarUrl())
                .build();
    }

    /**
     * Get a single user by ID.
     */
    public AdminUserDto getUser(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return toAdminUserDto(student);
    }

    /**
     * Admin awards XP directly to a student.
     */
    @Transactional
    public AdminUserDto awardXp(Long studentId, int xp, String reason) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        xpService.awardXp(student, xp,
                reason != null ? reason : "XP awarded by admin", "🎁");

        return toAdminUserDto(student);
    }

    /**
     * Admin directly awards (or revokes) a sub-badge for a student.
     */
    @Transactional
    public AdminUserDto awardSubBadge(Long studentId, Long subBadgeId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        SubBadge subBadge = subBadgeRepository.findById(subBadgeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub-badge not found"));

        StudentSubBadge progress = studentSubBadgeRepository
                .findByStudentIdAndSubBadgeId(studentId, subBadgeId)
                .orElseGet(() -> StudentSubBadge.builder()
                        .student(student)
                        .subBadge(subBadge)
                        .earned(false)
                        .build());

        if (!progress.isEarned()) {
            progress.setEarned(true);
            progress.setEarnedDate(java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MMM yyyy")));
            studentSubBadgeRepository.save(progress);

            // Award the sub-badge's XP
            xpService.awardXp(student, subBadge.getXpReward(),
                    "Earned sub-badge: " + subBadge.getName(), "🏅");
        }

        return toAdminUserDto(student);
    }
}
