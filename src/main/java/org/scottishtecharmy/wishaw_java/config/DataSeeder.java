package org.scottishtecharmy.wishaw_java.config;

import org.scottishtecharmy.wishaw_java.model.*;
import org.scottishtecharmy.wishaw_java.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final BadgeLevelRepository badgeLevelRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final CentreRepository centreRepository;
    private final TeamRepository teamRepository;
    private final XpEventRepository xpEventRepository;
    private final StudentSubBadgeRepository studentSubBadgeRepository;

    public DataSeeder(StudentRepository studentRepository,
                      PasswordEncoder passwordEncoder,
                      BadgeLevelRepository badgeLevelRepository,
                      MainBadgeRepository mainBadgeRepository,
                      SubBadgeRepository subBadgeRepository,
                      CentreRepository centreRepository,
                      TeamRepository teamRepository,
                      XpEventRepository xpEventRepository,
                      StudentSubBadgeRepository studentSubBadgeRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.badgeLevelRepository = badgeLevelRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.subBadgeRepository = subBadgeRepository;
        this.centreRepository = centreRepository;
        this.teamRepository = teamRepository;
        this.xpEventRepository = xpEventRepository;
        this.studentSubBadgeRepository = studentSubBadgeRepository;
    }

    @Override
    public void run(String... args) {
        seedBadgeLevels();
        seedMainBadges();
        seedSubBadges();
        seedCentres();
        seedTeams();
        seedDefaultUsers();
        seedXpEvents();
        seedStudentSubBadges();
    }

    /**
     * Seed the 4 badge levels with XP thresholds from instructions.md:
     * Bronze: 0-30, Silver: 31-70, Gold: 71-120, Platinum: 121+
     */
    private void seedBadgeLevels() {
        if (badgeLevelRepository.count() == 0) {
            badgeLevelRepository.save(BadgeLevel.builder()
                    .name("bronze")
                    .label("Bronze")
                    .minXP(0)
                    .maxXP(30)
                    .color("#cd7f32")
                    .icon("🥉")
                    .sortOrder(1)
                    .build());

            badgeLevelRepository.save(BadgeLevel.builder()
                    .name("silver")
                    .label("Silver")
                    .minXP(31)
                    .maxXP(70)
                    .color("#a8a9ad")
                    .icon("🥈")
                    .sortOrder(2)
                    .build());

            badgeLevelRepository.save(BadgeLevel.builder()
                    .name("gold")
                    .label("Gold")
                    .minXP(71)
                    .maxXP(120)
                    .color("#ffd700")
                    .icon("🥇")
                    .sortOrder(3)
                    .build());

            badgeLevelRepository.save(BadgeLevel.builder()
                    .name("platinum")
                    .label("Platinum")
                    .minXP(121)
                    .maxXP(null)
                    .color("#e5e4e2")
                    .icon("💎")
                    .sortOrder(4)
                    .build());

            System.out.println("==> Badge levels seeded (Bronze, Silver, Gold, Platinum)");
        }
    }

    /**
     * Seed the 5 main badges from the YMCA Esports badging system:
     * Game Mastery, Teamwork, Esports Citizen, Personal Development, Digital Skills
     */
    private void seedMainBadges() {
        if (mainBadgeRepository.count() == 0) {
            mainBadgeRepository.save(MainBadge.builder()
                    .slug("game-mastery")
                    .name("Game Mastery")
                    .icon("🎮")
                    .tagline("Learn, strategise, and dominate the game.")
                    .description("Game Mastery involves young gamers learning game mechanics, developing strategies, and making informed decisions during gameplay. Young people work through structured modules to improve their understanding of their chosen game, building consistent performance through practice, review, and reflection.")
                    .build());

            mainBadgeRepository.save(MainBadge.builder()
                    .slug("teamwork")
                    .name("Teamwork")
                    .icon("🤝")
                    .tagline("Achieve more together than apart.")
                    .description("Teamwork is when young people work together by sharing goals, supporting each other, and completing tasks to achieve a common outcome, both in games and in real life. This badge develops cooperation, communication, and collective responsibility — skills that transfer directly from esports into everyday situations.")
                    .build());

            mainBadgeRepository.save(MainBadge.builder()
                    .slug("esports-citizen")
                    .name("Esports Citizen")
                    .icon("🌐")
                    .tagline("Compete with integrity and represent your community.")
                    .description("Esports Citizen is where young people learn how to participate online in a positive way, supporting positive competition, being able to communicate appropriately, and creating a code of conduct in their groups and teams. It covers sportsmanship, respectful interactions, and becoming a responsible member of the esports community.")
                    .build());

            mainBadgeRepository.save(MainBadge.builder()
                    .slug("personal-development")
                    .name("Personal Development")
                    .icon("🌱")
                    .tagline("Reflect, grow, and set new goals.")
                    .description("Personal Development is used to improve young people's skills, building confidence and self-awareness. It involves young people identifying, reviewing, and reflecting on their mistakes, setting new goals, and focusing on improving their performance. This badge encourages a growth mindset and the habits of continuous improvement.")
                    .build());

            mainBadgeRepository.save(MainBadge.builder()
                    .slug("digital-skills")
                    .name("Digital Skills")
                    .icon("💻")
                    .tagline("Stay safe, stay savvy, and create online.")
                    .description("Digital Skills involve young people learning how to stay safe online and understanding how to use online tools. It also includes developing confidence in using technology, communicating responsibly, and using digital platforms to learn and create. This badge prepares young people to navigate the digital world safely and effectively.")
                    .build());

            System.out.println("==> Main badges seeded (Game Mastery, Teamwork, Esports Citizen, Personal Development, Digital Skills)");
        }
    }

    /**
     * Seed the sub-badges (challenges) for each main badge.
     * Each sub-badge awards XP towards its parent main badge.
     */
    private void seedSubBadges() {
        if (subBadgeRepository.count() == 0) {
            // Game Mastery sub-badges
            MainBadge gameMastery = mainBadgeRepository.findBySlug("game-mastery").orElseThrow();
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🗺️").name("Zone Reader")
                    .shortDesc("Read the zone and rotate safely.")
                    .criteria("Demonstrate correct zone rotation 3 sessions in a row.")
                    .xpReward(25).type("activity")
                    .skills("Problem Solving,Strategic Thinking")
                    .mainBadge(gameMastery).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🧱").name("Builder")
                    .shortDesc("Master building mechanics under pressure.")
                    .criteria("Complete all building drill challenges in the Fortnite Fundamentals module.")
                    .xpReward(25).type("activity")
                    .skills("Practical Skills,Perseverance")
                    .mainBadge(gameMastery).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🎯").name("Aim Trainer")
                    .shortDesc("Sharpen your aim with consistent practice.")
                    .criteria("Hit 70%+ accuracy in the aim drill session.")
                    .xpReward(30).type("activity")
                    .skills("Focus,Technical Ability")
                    .mainBadge(gameMastery).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("📋").name("Tactician")
                    .shortDesc("Plan and communicate a winning endgame strategy.")
                    .criteria("Present a winning endgame callout plan to the group.")
                    .xpReward(30).type("lesson")
                    .skills("Strategic Thinking,Communication")
                    .mainBadge(gameMastery).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🔍").name("Scout")
                    .shortDesc("Research and analyse your opponents.")
                    .criteria("Complete a full opponent scouting report.")
                    .xpReward(20).type("lesson")
                    .skills("Research,Critical Thinking")
                    .mainBadge(gameMastery).build());

            // Teamwork sub-badges
            MainBadge teamwork = mainBadgeRepository.findBySlug("teamwork").orElseThrow();
            subBadgeRepository.save(SubBadge.builder()
                    .icon("📢").name("Callout King")
                    .shortDesc("Call out enemy positions clearly and accurately.")
                    .criteria("Land 10 accurate callouts during a scrimmage session.")
                    .xpReward(20).type("activity")
                    .skills("Communication,Situational Awareness")
                    .mainBadge(teamwork).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("👨‍🏫").name("Coach")
                    .shortDesc("Guide a teammate through a challenge.")
                    .criteria("Successfully coach a peer through a module challenge.")
                    .xpReward(30).type("lesson")
                    .skills("Leadership,Empathy")
                    .mainBadge(teamwork).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🏅").name("Leader")
                    .shortDesc("Step up and captain your team.")
                    .criteria("Captain the team during a tournament match.")
                    .xpReward(25).type("activity")
                    .skills("Leadership,Decision Making")
                    .mainBadge(teamwork).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🤲").name("Supporter")
                    .shortDesc("Lift a struggling teammate over several sessions.")
                    .criteria("Actively support a struggling teammate across 3 sessions.")
                    .xpReward(20).type("activity")
                    .skills("Empathy,Inclusivity")
                    .mainBadge(teamwork).build());

            // Esports Citizen sub-badges
            MainBadge esportsCitizen = mainBadgeRepository.findBySlug("esports-citizen").orElseThrow();
            subBadgeRepository.save(SubBadge.builder()
                    .icon("📜").name("Code Maker")
                    .shortDesc("Write the rules your team plays by.")
                    .criteria("Help write and agree a team code of conduct.")
                    .xpReward(20).type("lesson")
                    .skills("Citizenship,Collaboration")
                    .mainBadge(esportsCitizen).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🕊️").name("Peacekeeper")
                    .shortDesc("Handle conflict calmly and constructively.")
                    .criteria("Resolve an in-game conflict constructively during a session.")
                    .xpReward(20).type("activity")
                    .skills("Conflict Resolution,Communication")
                    .mainBadge(esportsCitizen).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🧘").name("Tilt-Proof")
                    .shortDesc("Stay positive even when things go wrong.")
                    .criteria("Maintain positive language and attitude across 3 consecutive losing sessions.")
                    .xpReward(30).type("activity")
                    .skills("Emotional Regulation,Resilience")
                    .mainBadge(esportsCitizen).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🏟️").name("Good Sport")
                    .shortDesc("Be the player everyone respects.")
                    .criteria("Demonstrate exemplary sportsmanship at a tournament (nominated by a coach).")
                    .xpReward(25).type("activity")
                    .skills("Integrity,Respect")
                    .mainBadge(esportsCitizen).build());

            // Personal Development sub-badges
            MainBadge personalDevelopment = mainBadgeRepository.findBySlug("personal-development").orElseThrow();
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🔄").name("Reflector")
                    .shortDesc("Review your performance honestly after every session.")
                    .criteria("Complete a written self-reflection after each session for one full module.")
                    .xpReward(25).type("lesson")
                    .skills("Self-Awareness,Reflection")
                    .mainBadge(personalDevelopment).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🎯").name("Goal Setter")
                    .shortDesc("Set targets and check in on your progress.")
                    .criteria("Set SMART goals at the start of a module and review them at the end.")
                    .xpReward(20).type("lesson")
                    .skills("Goal Setting,Planning")
                    .mainBadge(personalDevelopment).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("💬").name("Feedback Taker")
                    .shortDesc("Use coach feedback to visibly improve.")
                    .criteria("Receive feedback from a coach and demonstrate an improvement the following session.")
                    .xpReward(25).type("activity")
                    .skills("Adaptability,Openness to Learning")
                    .mainBadge(personalDevelopment).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🚀").name("Level Up")
                    .shortDesc("Track a stat and grow it measurably over time.")
                    .criteria("Improve a tracked personal stat by at least 20% over 4 weeks.")
                    .xpReward(30).type("activity")
                    .skills("Perseverance,Self-Improvement")
                    .mainBadge(personalDevelopment).build());

            // Digital Skills sub-badges
            MainBadge digitalSkills = mainBadgeRepository.findBySlug("digital-skills").orElseThrow();
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🛡️").name("Safe Surfer")
                    .shortDesc("Know the risks and stay safe online.")
                    .criteria("Complete the online safety module and pass the end quiz.")
                    .xpReward(20).type("lesson")
                    .skills("Online Safety,Digital Literacy")
                    .mainBadge(digitalSkills).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🔐").name("Data Guardian")
                    .shortDesc("Protect your data and personal information.")
                    .criteria("Demonstrate understanding of password hygiene and data privacy.")
                    .xpReward(20).type("lesson")
                    .skills("Digital Literacy,Responsibility")
                    .mainBadge(digitalSkills).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("🎥").name("Content Creator")
                    .shortDesc("Create and share something about your esports journey.")
                    .criteria("Create and share a piece of digital content (clip, post, or presentation) about your esports journey.")
                    .xpReward(30).type("activity")
                    .skills("Creativity,Digital Communication")
                    .mainBadge(digitalSkills).build());
            subBadgeRepository.save(SubBadge.builder()
                    .icon("📈").name("Analyst")
                    .shortDesc("Use data tools to track and present your stats.")
                    .criteria("Use a digital stats tracker for 4 consecutive weeks and present your findings.")
                    .xpReward(25).type("activity")
                    .skills("Data Literacy,Critical Thinking")
                    .mainBadge(digitalSkills).build());

            System.out.println("==> Sub-badges seeded (21 challenges across 5 main badges)");
        }
    }

    /**
     * Seed the centres (hubs) to match the frontend mock data.
     */
    private void seedCentres() {
        if (centreRepository.count() == 0) {
            centreRepository.save(Centre.builder().name("Hub Glasgow").icon("🏴󠁧󠁢󠁳󠁣󠁴󠁿").build());
            centreRepository.save(Centre.builder().name("Hub Edinburgh").icon("🏰").build());
            centreRepository.save(Centre.builder().name("Hub Manchester").icon("🐝").build());
            centreRepository.save(Centre.builder().name("Hub Birmingham").icon("⚙️").build());
            centreRepository.save(Centre.builder().name("Hub Liverpool").icon("🎸").build());
            System.out.println("==> Centres seeded (Glasgow, Edinburgh, Manchester, Birmingham, Liverpool)");
        }
    }

    /**
     * Seed teams to match frontend mock data.
     */
    private void seedTeams() {
        if (teamRepository.count() == 0) {
            Centre glasgow = centreRepository.findAll().stream()
                    .filter(c -> "Hub Glasgow".equals(c.getName())).findFirst().orElse(null);
            Centre edinburgh = centreRepository.findAll().stream()
                    .filter(c -> "Hub Edinburgh".equals(c.getName())).findFirst().orElse(null);

            teamRepository.save(Team.builder()
                    .slug("team-1")
                    .name("The Code Warriors")
                    .icon("⚔️")
                    .colour("#3b82f6")
                    .hub("Hub Glasgow")
                    .founded("2024")
                    .description("Elite squad of strategic thinkers who dominate through teamwork and precision.")
                    .game("Fortnite")
                    .centre(glasgow)
                    .build());

            teamRepository.save(Team.builder()
                    .slug("wolf-cubs")
                    .name("Wolf Cubs")
                    .icon("🐺")
                    .colour("#4f8ef7")
                    .hub("Hub Edinburgh")
                    .founded("2024")
                    .description("Young and hungry squad ready to prove themselves.")
                    .game("Fortnite")
                    .centre(edinburgh)
                    .build());

            teamRepository.save(Team.builder()
                    .slug("phoenix-rising")
                    .name("Phoenix Rising")
                    .icon("🔥")
                    .colour("#ef4444")
                    .hub("Hub Glasgow")
                    .founded("2025")
                    .description("Rising from the ashes to claim victory.")
                    .game("Rocket League")
                    .centre(glasgow)
                    .build());

            System.out.println("==> Teams seeded (Code Warriors, Wolf Cubs, Phoenix Rising)");
        }
    }

    private void seedDefaultUsers() {
        Centre glasgow = centreRepository.findAll().stream()
                .filter(c -> "Hub Glasgow".equals(c.getName())).findFirst().orElse(null);
        Centre edinburgh = centreRepository.findAll().stream()
                .filter(c -> "Hub Edinburgh".equals(c.getName())).findFirst().orElse(null);
        Centre manchester = centreRepository.findAll().stream()
                .filter(c -> "Hub Manchester".equals(c.getName())).findFirst().orElse(null);
        Centre birmingham = centreRepository.findAll().stream()
                .filter(c -> "Hub Birmingham".equals(c.getName())).findFirst().orElse(null);

        Team codeWarriors = teamRepository.findBySlug("team-1").orElse(null);
        Team wolfCubs = teamRepository.findBySlug("wolf-cubs").orElse(null);

        // Admin user
        if (studentRepository.findByUsername("admin").isEmpty()) {
            Student admin = Student.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .passwordHint("default admin password")
                    .gamertag("Admin")
                    .realName("Default Admin")
                    .role("ROLE_ADMIN")
                    .level(1)
                    .xp(0)
                    .joinedDate("Mar 2026")
                    .build();
            studentRepository.save(admin);
            System.out.println("==> Default admin user created (username: admin / password: admin123)");
        }

        // Main demo student — matches frontend mock data studentId: 1
        if (studentRepository.findByUsername("@alex_gamer").isEmpty()) {
            Student alex = Student.builder()
                    .username("@alex_gamer")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .passwordHint("same as test account")
                    .email("alex@example.com")
                    .gamertag("Tiger Bear")
                    .realName("Alex Johnson")
                    .bio("Fortnite enthusiast and strategy lover. Always looking to improve!")
                    .role("ROLE_STUDENT")
                    .level(12)
                    .xp(1245)
                    .joinedDate("Sep 2024")
                    .centre(glasgow)
                    .team(codeWarriors)
                    .captain(false)
                    .build();
            studentRepository.save(alex);
            System.out.println("==> Demo student @alex_gamer created (password: password123)");
        }

        // Leaderboard players (matching frontend mock data)
        createStudentIfMissing("@jordan_r", "J-Force", "Jordan Raines", 18, 2100, edinburgh, wolfCubs, true);
        createStudentIfMissing("@priya_k", "Strategy Queen", "Priya Kapoor", 16, 1980, manchester, null, false);
        createStudentIfMissing("@callum_s", "Callum Clutch", "Callum Shaw", 15, 1750, glasgow, codeWarriors, true);
        createStudentIfMissing("@mei_l", "Mei Dragon", "Mei Lin", 12, 1190, birmingham, null, false);
        createStudentIfMissing("@tyler_b", "TylerTech", "Tyler Braun", 11, 1050, manchester, null, false);
        createStudentIfMissing("@aisha_o", "Aisha Ace", "Aisha Okonkwo", 10, 980, edinburgh, wolfCubs, false);
        createStudentIfMissing("@rory_mac", "Rory Mac", "Rory MacDonald", 10, 870, glasgow, null, false);
        createStudentIfMissing("@zara_p", "Zara Storm", "Zara Patel", 9, 760, birmingham, null, false);
        createStudentIfMissing("@finn_ob", "Finn Fire", "Finn O'Brien", 8, 640, edinburgh, null, false);
        createStudentIfMissing("@sasha_i", "Sasha Shadow", "Sasha Ivanova", 7, 510, manchester, null, false);
        createStudentIfMissing("@kwame_a", "Kwame", "Kwame Asante", 5, 320, glasgow, null, false);

        System.out.println("==> Leaderboard demo students seeded");
    }

    private void createStudentIfMissing(String username, String gamertag, String realName,
                                        int level, int xp, Centre centre, Team team, boolean captain) {
        if (studentRepository.findByUsername(username).isEmpty()) {
            Student student = Student.builder()
                    .username(username)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .passwordHint("default password")
                    .gamertag(gamertag)
                    .realName(realName)
                    .role("ROLE_STUDENT")
                    .level(level)
                    .xp(xp)
                    .joinedDate("Sep 2024")
                    .centre(centre)
                    .team(team)
                    .captain(captain)
                    .build();
            studentRepository.save(student);
        }
    }

    /**
     * Seed XP events for the demo student to show recent activity.
     */
    private void seedXpEvents() {
        Student alex = studentRepository.findByUsername("@alex_gamer").orElse(null);
        if (alex != null && xpEventRepository.findByStudentIdOrderByDateDesc(alex.getId()).isEmpty()) {
            xpEventRepository.saveAll(List.of(
                    XpEvent.builder().student(alex).activity("Weekly challenge completed").xp(75).date("2025-03-21").icon("🎯").build(),
                    XpEvent.builder().student(alex).activity("Reached Level 10").xp(50).date("2025-03-07").icon("⭐").build(),
                    XpEvent.builder().student(alex).activity("Won monthly tournament").xp(300).date("2025-02-14").icon("👑").build(),
                    XpEvent.builder().student(alex).activity("Top scorer 3 sessions in a row").xp(150).date("2025-01-22").icon("🎯").build(),
                    XpEvent.builder().student(alex).activity("Tournament semi-final win").xp(200).date("2025-01-09").icon("🏅").build()
            ));
            System.out.println("==> XP events seeded for @alex_gamer");
        }
    }

    /**
     * Seed student sub-badge progress for the demo student.
     */
    private void seedStudentSubBadges() {
        Student alex = studentRepository.findByUsername("@alex_gamer").orElse(null);
        if (alex == null) return;
        if (!studentSubBadgeRepository.findByStudentId(alex.getId()).isEmpty()) return;

        List<SubBadge> allSubBadges = subBadgeRepository.findAll();
        
        // Create progress entries for all sub-badges
        // Mark some as earned to match frontend mock (3 earned out of 21)
        int earned = 0;
        for (SubBadge sb : allSubBadges) {
            boolean isEarned = earned < 3; // First 3 are earned
            String earnedDate = isEarned ? "Nov 2024" : null;
            
            studentSubBadgeRepository.save(StudentSubBadge.builder()
                    .student(alex)
                    .subBadge(sb)
                    .earned(isEarned)
                    .earnedDate(earnedDate)
                    .build());
            
            if (isEarned) earned++;
        }
        
        // Also seed progress for other leaderboard students
        List<Student> otherStudents = studentRepository.findAllByOrderByXpDesc().stream()
                .filter(s -> !s.getUsername().equals("@alex_gamer") && !s.getRole().equals("ROLE_ADMIN"))
                .toList();
        
        for (Student student : otherStudents) {
            if (!studentSubBadgeRepository.findByStudentId(student.getId()).isEmpty()) continue;
            
            // Give higher-ranked students more progress
            int earnedCount = Math.max(0, (student.getLevel() - 5) / 2);
            int idx = 0;
            for (SubBadge sb : allSubBadges) {
                boolean studentEarned = idx < earnedCount;
                studentSubBadgeRepository.save(StudentSubBadge.builder()
                        .student(student)
                        .subBadge(sb)
                        .earned(studentEarned)
                        .earnedDate(studentEarned ? "Jan 2025" : null)
                        .build());
                idx++;
            }
        }
        
        System.out.println("==> Student sub-badge progress seeded");
    }
}
