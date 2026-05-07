package org.scottishtecharmy.wishaw_java.config;

import org.scottishtecharmy.wishaw_java.dto.TournamentDtos;
import org.scottishtecharmy.wishaw_java.entity.CaloriesLog;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.GameGroup;
import org.scottishtecharmy.wishaw_java.entity.LearningModule;
import org.scottishtecharmy.wishaw_java.entity.MainBadge;
import org.scottishtecharmy.wishaw_java.entity.MatchParticipant;
import org.scottishtecharmy.wishaw_java.entity.MatchRecord;
import org.scottishtecharmy.wishaw_java.entity.ModuleSessionItem;
import org.scottishtecharmy.wishaw_java.entity.NotificationRecord;
import org.scottishtecharmy.wishaw_java.entity.Sport;
import org.scottishtecharmy.wishaw_java.entity.SubBadge;
import org.scottishtecharmy.wishaw_java.entity.Team;
import org.scottishtecharmy.wishaw_java.entity.TeamMember;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.TournamentParticipant;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.entity.UserSubBadge;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import org.scottishtecharmy.wishaw_java.enums.MatchStatus;
import org.scottishtecharmy.wishaw_java.enums.ModuleStatus;
import org.scottishtecharmy.wishaw_java.enums.NotificationType;
import org.scottishtecharmy.wishaw_java.enums.ParticipantStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.enums.TournamentType;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.CaloriesLogRepository;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.GameGroupRepository;
import org.scottishtecharmy.wishaw_java.repository.LearningModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.MainBadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleSessionItemRepository;
import org.scottishtecharmy.wishaw_java.repository.NotificationRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.SportRepository;
import org.scottishtecharmy.wishaw_java.repository.SubBadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.TeamMemberRepository;
import org.scottishtecharmy.wishaw_java.repository.TeamRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentParticipantRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.repository.UserProfileRepository;
import org.scottishtecharmy.wishaw_java.repository.UserSubBadgeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@Transactional
public class DataSeeder implements ApplicationRunner {

    private final AppProperties appProperties;
    private final CentreRepository centreRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final GameGroupRepository gameGroupRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final ModuleSessionItemRepository moduleSessionItemRepository;
    private final SportRepository sportRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserSubBadgeRepository userSubBadgeRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final CaloriesLogRepository caloriesLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiMapper apiMapper;

    public DataSeeder(AppProperties appProperties,
                      CentreRepository centreRepository,
                      UserAccountRepository userAccountRepository,
                      UserProfileRepository userProfileRepository,
                      GameGroupRepository gameGroupRepository,
                      MainBadgeRepository mainBadgeRepository,
                      LearningModuleRepository learningModuleRepository,
                      SubBadgeRepository subBadgeRepository,
                      ModuleSessionItemRepository moduleSessionItemRepository,
                      SportRepository sportRepository,
                      TournamentRepository tournamentRepository,
                      TournamentParticipantRepository tournamentParticipantRepository,
                      MatchRecordRepository matchRecordRepository,
                      MatchParticipantRepository matchParticipantRepository,
                      TeamRepository teamRepository,
                      TeamMemberRepository teamMemberRepository,
                      UserSubBadgeRepository userSubBadgeRepository,
                      NotificationRecordRepository notificationRecordRepository,
                      CaloriesLogRepository caloriesLogRepository,
                      PasswordEncoder passwordEncoder,
                      ApiMapper apiMapper) {
        this.appProperties = appProperties;
        this.centreRepository = centreRepository;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.gameGroupRepository = gameGroupRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.learningModuleRepository = learningModuleRepository;
        this.subBadgeRepository = subBadgeRepository;
        this.moduleSessionItemRepository = moduleSessionItemRepository;
        this.sportRepository = sportRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userSubBadgeRepository = userSubBadgeRepository;
        this.notificationRecordRepository = notificationRecordRepository;
        this.caloriesLogRepository = caloriesLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.apiMapper = apiMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
                if (!appProperties.getSeed().isEnabled()) {
            return;
        }

                Centre c1 = upsertCentre("c1", "Wishaw YMCA", "Wishaw, Scotland");
                Centre c2 = upsertCentre("c2", "Glasgow YMCA", "Glasgow, Scotland");

                UserAccount u1 = upsertUser("u1", "admin@wymca.org", "admin123", UserRole.ADMIN, c1);
                UserAccount u2 = upsertUser("u2", "player1@wymca.org", "player123", UserRole.PLAYER, c1);
                UserAccount u3 = upsertUser("u3", "player2@wymca.org", "player123", UserRole.PLAYER, c1);

                upsertProfile(u1, "Emma W", "Emma", "Williamson", LocalDate.parse("1990-05-14"), "Centre Manager & COO", true);
                upsertProfile(u2, "Player One", "Alex", "Smith", LocalDate.parse("2011-03-17"), "Fortnite fanatic", true);
                upsertProfile(u3, "Player Two", "Jamie", "Lee", LocalDate.parse("2012-08-30"), "Minecraft builder", false);

                if (mainBadgeRepository.count() > 0 || learningModuleRepository.count() > 0 || tournamentRepository.count() > 0) {
                        return;
                }

        gameGroupRepository.saveAll(List.of(
                new GameGroup("g1", "Fortnite Juniors", "Fortnite", 12, c1),
                new GameGroup("g2", "Minecraft Survival", "Minecraft", 8, c1),
                new GameGroup("g3", "Rocket League", "Rocket League", 10, c1)
        ));

        MainBadge mb1 = mainBadgeRepository.save(new MainBadge("mb1", "Game Mastery", "Learning game mechanics, developing strategies, and making informed decisions during gameplay.", "\uD83C\uDFAE"));
        MainBadge mb2 = mainBadgeRepository.save(new MainBadge("mb2", "Teamwork", "Working together by sharing goals, supporting each other, and completing tasks to achieve a common outcome.", "\uD83E\uDD1D"));
        MainBadge mb3 = mainBadgeRepository.save(new MainBadge("mb3", "Esports Citizen", "Participating online positively, supporting fair competition, communicating appropriately.", "\uD83C\uDF10"));
        MainBadge mb4 = mainBadgeRepository.save(new MainBadge("mb4", "Personal Development", "Building confidence, self-awareness, reflecting on mistakes, setting goals and improving performance.", "\uD83C\uDF1F"));
        MainBadge mb5 = mainBadgeRepository.save(new MainBadge("mb5", "Digital Skills", "Learning to stay safe online, using digital tools, communicating responsibly, and creating on digital platforms.", "\uD83D\uDCBB"));

        LearningModule m1 = learningModuleRepository.save(new LearningModule("m1", "Road to Diamond", "Rocket League", "Rank-based challenges, mechanics masterclass, and team building for Rocket League players.", 12, ModuleStatus.ACTIVE));
        LearningModule m2 = learningModuleRepository.save(new LearningModule("m2", "Defeat the Ender Dragon", "Minecraft", "Survival challenges, build battles, and teamwork to defeat the Ender Dragon.", 12, ModuleStatus.ACTIVE));

        SubBadge sb1 = subBadgeRepository.save(subBadge("sb1", "Skill Evaluation", "Take part in an individual skills evaluation session", mb1, 6, List.of("Confidence", "Resilience", "Decision Making", "Communication"), m1));
        SubBadge sb2 = subBadgeRepository.save(subBadge("sb2", "Master Plan", "Create a game plan in relation to drop spot, rotation and loadouts", mb1, 6, List.of("Planning & Organising", "Decision Making", "Problem Solving", "Working with Others"), m1));
        SubBadge sb3 = subBadgeRepository.save(subBadge("sb3", "Basic Mechanics", "Demonstrate basic mechanics", mb1, 6, List.of("Planning & Organising", "Decision Making", "Problem Solving"), m1));
        SubBadge sb4 = subBadgeRepository.save(subBadge("sb4", "Duo Snatcher", "Contribute to team name and agree on conduct", mb2, 3, List.of("Working with Others", "Creativity", "Confidence", "Leadership"), m1));
        SubBadge sb5 = subBadgeRepository.save(subBadge("sb5", "Tactical Minds", "Create communications strategy", mb2, 6, List.of("Communication", "Confidence", "Working with Others", "Planning & Organising"), m1));
        SubBadge sb6 = subBadgeRepository.save(subBadge("sb6", "Role Model", "Understand your role within your team", mb2, 6, List.of("Working with Others", "Self-Management", "Confidence", "Problem Solving"), m1));
        SubBadge sb7 = subBadgeRepository.save(subBadge("sb7", "Learn In Action", "Focus on previous mistakes by practising improvements in a competitive match", mb4, 6, List.of("Decision Making", "Confidence", "Leadership"), m1));
        SubBadge sb8 = subBadgeRepository.save(subBadge("sb8", "Goal Scorer", "Set a new goal", mb4, 6, List.of("Resilience", "Confidence", "Self-Management", "Creativity"), m1));
        SubBadge sb9 = subBadgeRepository.save(subBadge("sb9", "Player Performance", "Use replay review to identify mistakes in a competitive match", mb4, 6, List.of("Confidence", "Resilience", "Self-Management"), m1));
        SubBadge sb10 = subBadgeRepository.save(subBadge("sb10", "Conduct Creator", "Help create the group code of conduct", mb3, 6, List.of("Working with Others", "Resilience", "Problem Solving", "Self-Management"), m1));
        SubBadge sb11 = subBadgeRepository.save(subBadge("sb11", "Code Keeper", "Don't breach the group or duo conduct", mb3, 6, List.of("Self-Management", "Resilience", "Confidence"), m1));
        SubBadge sb12 = subBadgeRepository.save(subBadge("sb12", "Positive Voice", "Encourage other players or teams", mb3, 3, List.of("Working with Others", "Leadership", "Confidence", "Digital Literacy"), m1));
        SubBadge sb13 = subBadgeRepository.save(subBadge("sb13", "Data Logger", "Record stats in digital format", mb5, 6, List.of("Planning & Organising", "Creativity", "Digital Literacy"), m1));
        SubBadge sb14 = subBadgeRepository.save(subBadge("sb14", "Prep Master", "Use online tools to plan your strategy", mb5, 6, List.of("Planning & Organising", "Digital Literacy", "Decision Making"), m1));
        SubBadge sb15 = subBadgeRepository.save(subBadge("sb15", "Memory Maker", "Take screenshots, clips and create notes about your journey", mb5, 6, List.of("Digital Literacy", "Planning & Organising", "Working with Others"), m1));
        SubBadge sb16 = subBadgeRepository.save(subBadge("sb16", "End Of The Road", "Achieve diamond rank", mb1, 15, List.of("Digital Literacy", "Resilience", "Confidence"), m1));
        SubBadge sb17 = subBadgeRepository.save(subBadge("sb17", "Base Builder", "Help build and set up a shared homebase", mb1, 3, List.of("Problem Solving", "Planning & Organising", "Working with Others"), m2));
        SubBadge sb18 = subBadgeRepository.save(subBadge("sb18", "Nether Ready", "Gather weapons, armour, and food to journey to the nether", mb1, 6, List.of("Resilience", "Self-Management", "Decision Making", "Working with Others"), m2));
        SubBadge sb19 = subBadgeRepository.save(subBadge("sb19", "Dragon Prepared", "Understand and prepare for the Ender Dragon fight", mb1, 6, List.of("Planning & Organising", "Digital Literacy"), m2));
        SubBadge sb20 = subBadgeRepository.save(subBadge("sb20", "Dragon Slayer", "Kill the ender dragon and complete the game", mb1, 15, List.of("Digital Literacy", "Resilience", "Confidence"), m2));

        moduleSessionItemRepository.saveAll(List.of(
                session(1, "Welcome/Settle In", m1, null),
                session(2, "Code of Conduct", m1, sb10),
                session(3, "Player Evaluation", m1, sb1),
                session(4, "Player Evaluation cont.", m1, sb1),
                session(5, "Duo Allocation", m1, sb4),
                session(6, "Team Roles", m1, sb6),
                session(7, "Practice Routines", m1, sb3),
                session(8, "Drop spot & Loot paths", m1, sb2),
                session(9, "Goal Setting", m1, sb8),
                session(10, "Stat tracking", m1, sb13),
                session(11, "Ranked Grind", m1, sb16),
                session(12, "Ranked Grind", m1, sb16),
                session(1, "Welcome / Team Formation", m2, null),
                session(2, "Code of Conduct", m2, null),
                session(3, "Base Building", m2, sb17),
                session(4, "Resource Gathering", m2, null),
                session(5, "Nether Prep", m2, sb18),
                session(6, "Nether Expedition", m2, null),
                session(7, "Goal Setting", m2, null),
                session(8, "End Portal Prep", m2, sb19),
                session(9, "Practice Fights", m2, null),
                session(10, "Dragon Fight Attempt 1", m2, sb20),
                session(11, "Dragon Fight Attempt 2", m2, sb20),
                session(12, "Celebration & Reflection", m2, null)
        ));

        Sport s1 = sportRepository.save(sport("s1", "Rocket League", "\uD83D\uDE80", "Vehicular soccer video game", List.of(
                new TournamentDtos.ScoreFieldDto("goals", "Goals", "number"),
                new TournamentDtos.ScoreFieldDto("assists", "Assists", "number")
        )));
        Sport s2 = sportRepository.save(sport("s2", "Fortnite", "\uD83C\uDFAF", "Battle royale game", List.of(
                new TournamentDtos.ScoreFieldDto("kills", "Kills", "number"),
                new TournamentDtos.ScoreFieldDto("placement", "Placement", "number")
        )));
        sportRepository.save(sport("s3", "Minecraft", "\u26CF\uFE0F", "Sandbox adventure game", List.of(
                new TournamentDtos.ScoreFieldDto("objectives", "Objectives Completed", "number")
        )));

        Tournament t1 = tournamentRepository.save(tournament("t1", "Wishaw Rocket League Cup", s1, "Monthly Rocket League tournament for junior players", "Best of 3 matches", "Wishaw YMCA Esports Room 1", TournamentType.INDIVIDUAL, TournamentStatus.PUBLISHED, 16, 2, null, null, iso("2026-04-10T10:00:00Z"), iso("2026-04-10T16:00:00Z"), iso("2026-03-20T00:00:00Z"), iso("2026-04-08T23:59:59Z")));
        Tournament t2 = tournamentRepository.save(tournament("t2", "Fortnite Friday Showdown", s2, "Weekly Fortnite competitive night", null, "Wishaw YMCA Esports Room 2", TournamentType.TEAM, TournamentStatus.PUBLISHED, 24, 1, 2, 4, iso("2026-04-04T18:00:00Z"), iso("2026-04-04T21:00:00Z"), iso("2026-03-25T00:00:00Z"), iso("2026-04-03T23:59:59Z")));

        tournamentParticipantRepository.saveAll(List.of(
                TournamentParticipant.builder().tournament(t1).userAccount(u2).displayNameSnapshot("Player One").status(ParticipantStatus.REGISTERED).build(),
                TournamentParticipant.builder().tournament(t1).userAccount(u3).displayNameSnapshot("Player Two").status(ParticipantStatus.REGISTERED).build(),
                TournamentParticipant.builder().tournament(t2).userAccount(u2).displayNameSnapshot("Player One").status(ParticipantStatus.REGISTERED).build()
        ));

        MatchRecord match1 = matchRecordRepository.save(MatchRecord.builder()
                .id("match1")
                .tournament(t1)
                .roundLabel("Round 1 - Match 1")
                .scheduledAt(iso("2026-04-10T10:30:00Z"))
                .venue("Wishaw YMCA Esports Room 1")
                .status(MatchStatus.SCHEDULED)
                .build());
        matchParticipantRepository.saveAll(List.of(
                MatchParticipant.builder().matchRecord(match1).userAccount(u2).displayNameSnapshot("Player One").attendance(AttendanceStatus.PRESENT).build(),
                MatchParticipant.builder().matchRecord(match1).userAccount(u3).displayNameSnapshot("Player Two").attendance(AttendanceStatus.PRESENT).build()
        ));

        Team team1 = teamRepository.save(Team.builder().id("team1").name("Team Alpha").tournament(t2).build());
        teamMemberRepository.save(TeamMember.builder().team(team1).userAccount(u2).displayNameSnapshot("Player One").build());

        userSubBadgeRepository.saveAll(List.of(
                award(u2, sb1, "2026-03-01T00:00:00Z"),
                award(u2, sb2, "2026-03-02T00:00:00Z"),
                award(u2, sb3, "2026-03-03T00:00:00Z"),
                award(u2, sb4, "2026-03-04T00:00:00Z"),
                award(u2, sb5, "2026-03-05T00:00:00Z"),
                award(u2, sb6, "2026-03-06T00:00:00Z"),
                award(u2, sb7, "2026-03-07T00:00:00Z"),
                award(u2, sb8, "2026-03-08T00:00:00Z"),
                award(u2, sb10, "2026-03-09T00:00:00Z"),
                award(u2, sb12, "2026-03-10T00:00:00Z"),
                award(u2, sb13, "2026-03-11T00:00:00Z"),
                award(u2, sb14, "2026-03-12T00:00:00Z"),
                award(u2, sb16, "2026-03-13T00:00:00Z"),
                award(u3, sb17, "2026-03-05T00:00:00Z"),
                award(u3, sb18, "2026-03-06T00:00:00Z")
        ));

        notificationRecordRepository.saveAll(List.of(
                notification("n1", u2, NotificationType.TOURNAMENT, "New Tournament", "Wishaw Rocket League Cup is now open for registration!", false, "2026-03-25T12:00:00Z", "/tournaments/t1"),
                notification("n2", u2, NotificationType.BADGE, "Badge Earned!", "You earned the Skill Evaluation sub-badge!", false, "2026-03-24T15:00:00Z", "/badges"),
                notification("n3", u2, NotificationType.MODULE, "Module Update", "Road to Diamond module week 8 is coming up.", true, "2026-03-23T09:00:00Z", "/modules/m1"),
                notification("n4", u1, NotificationType.ANNOUNCEMENT, "Welcome!", "Tournament begins soon.", false, "2026-03-30T12:00:00Z", "/tournaments/t1")
        ));

        caloriesLogRepository.saveAll(List.of(
                CaloriesLog.builder().userAccount(u2).sportName("Rocket League").calories(500).loggedAt(Instant.parse("2026-03-28T10:00:00Z")).build(),
                CaloriesLog.builder().userAccount(u2).sportName("Fortnite").calories(750).loggedAt(Instant.parse("2026-03-29T10:00:00Z")).build()
        ));
    }

    private UserAccount user(String id, String email, String password, UserRole role, Centre centre) {
        return UserAccount.builder()
                .id(id)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .centre(centre)
                .build();
    }

    private UserProfile profile(UserAccount userAccount, String displayName, String firstName, String lastName, String bio, boolean allowSocialSharing) {
        return UserProfile.builder()
                .userAccount(userAccount)
                .displayName(displayName)
                .firstName(firstName)
                .lastName(lastName)
                .bio(bio)
                .photoUrl(null)
                .overlayTemplate(null)
                .showInPublicList(true)
                .allowSocialSharing(allowSocialSharing)
                .build();
    }

    private SubBadge subBadge(String id, String name, String description, MainBadge mainBadge, int points, List<String> skills, LearningModule learningModule) {
        return SubBadge.builder()
                .id(id)
                .name(name)
                .description(description)
                .mainBadge(mainBadge)
                .points(points)
                .skillsCsv(apiMapper.writeJson(skills))
                .learningModule(learningModule)
                .build();
    }

    private ModuleSessionItem session(int weekNo, String focus, LearningModule module, SubBadge subBadge) {
        return ModuleSessionItem.builder()
                .weekNo(weekNo)
                .focus(focus)
                .sessionPlanUrl(null)
                .slidesUrl(null)
                .learningModule(module)
                .subBadge(subBadge)
                .build();
    }

    private Sport sport(String id, String name, String icon, String description, List<TournamentDtos.ScoreFieldDto> scoreFields) {
        return Sport.builder()
                .id(id)
                .name(name)
                .icon(icon)
                .description(description)
                .scoreFieldsJson(apiMapper.writeJson(scoreFields))
                .rankingWin(3)
                .rankingDraw(1)
                .rankingLoss(0)
                .build();
    }

    private Tournament tournament(String id,
                                  String name,
                                  Sport sport,
                                  String description,
                                  String rules,
                                  String venue,
                                  TournamentType type,
                                  TournamentStatus status,
                                  int capacity,
                                  int participantCount,
                                  Integer teamMinSize,
                                  Integer teamMaxSize,
                                  LocalDateTime startDate,
                                  LocalDateTime endDate,
                                  LocalDateTime regStartDate,
                                  LocalDateTime regEndDate) {
        return Tournament.builder()
                .id(id)
                .name(name)
                .sport(sport)
                .description(description)
                .rulesText(rules)
                .venue(venue)
                .type(type)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .regStartDate(regStartDate)
                .regEndDate(regEndDate)
                .capacity(capacity)
                .participantCount(participantCount)
                .teamMinSize(teamMinSize)
                .teamMaxSize(teamMaxSize)
                .pointsWin(3)
                .pointsDraw(1)
                .pointsLoss(0)
                .build();
    }

        private Centre upsertCentre(String id, String name, String location) {
                Centre centre = centreRepository.findById(id).orElseGet(() -> new Centre(id, name, location));
                centre.setName(name);
                centre.setLocation(location);
                return centreRepository.save(centre);
        }

        private UserAccount upsertUser(String id, String email, String password, UserRole role, Centre centre) {
                UserAccount userAccount = userAccountRepository.findById(id)
                                .or(() -> userAccountRepository.findByEmailIgnoreCase(email))
                                .orElseGet(UserAccount::new);
                if (userAccount.getId() == null || userAccount.getId().isBlank()) {
                        userAccount.setId(id);
                }
                userAccount.setEmail(email);
                userAccount.setPasswordHash(passwordEncoder.encode(password));
                userAccount.setRole(role);
                userAccount.setCentre(centre);
                return userAccountRepository.save(userAccount);
        }

        private void upsertProfile(UserAccount userAccount,
                                                           String displayName,
                                                           String firstName,
                                                           String lastName,
                                                           LocalDate dateOfBirth,
                                                           String bio,
                                                           boolean allowSocialSharing) {
                UserProfile userProfile = userProfileRepository.findById(userAccount.getId())
                                .orElseGet(() -> UserProfile.builder().userAccount(userAccount).build());
                userProfile.setUserAccount(userAccount);
                userProfile.setDisplayName(displayName);
                userProfile.setFirstName(firstName);
                userProfile.setLastName(lastName);
                userProfile.setDateOfBirth(dateOfBirth);
                userProfile.setBio(bio);
                if (userProfile.getPhotoUrl() == null) {
                        userProfile.setPhotoUrl(null);
                }
                if (userProfile.getOverlayTemplate() == null) {
                        userProfile.setOverlayTemplate(null);
                }
                userProfile.setShowInPublicList(true);
                userProfile.setAllowSocialSharing(allowSocialSharing);
                userProfileRepository.save(userProfile);
        }

    private UserSubBadge award(UserAccount userAccount, SubBadge subBadge, String awardedAt) {
        return UserSubBadge.builder()
                .userAccount(userAccount)
                .subBadge(subBadge)
                .awardedAt(Instant.parse(awardedAt))
                .build();
    }

    private NotificationRecord notification(String id,
                                            UserAccount userAccount,
                                            NotificationType type,
                                            String title,
                                            String message,
                                            boolean isRead,
                                            String createdAt,
                                            String linkTo) {
        return NotificationRecord.builder()
                .id(id)
                .userAccount(userAccount)
                .type(type)
                .title(title)
                .message(message)
                .isRead(isRead)
                .createdAt(Instant.parse(createdAt))
                .linkTo(linkTo)
                .build();
    }

    private LocalDateTime iso(String value) {
        return OffsetDateTime.parse(value).toLocalDateTime();
    }
}
