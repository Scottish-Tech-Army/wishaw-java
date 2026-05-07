package org.scottishtecharmy.wishaw_java.config;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.BadgeLevel;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.Group;
import org.scottishtecharmy.wishaw_java.entity.Module;
import org.scottishtecharmy.wishaw_java.entity.ParentLink;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.BadgeLevelRepository;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeRepository;
import org.scottishtecharmy.wishaw_java.repository.GroupRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.ParentLinkRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final BadgeCategoryRepository badgeCategoryRepo;
    private final BadgeLevelRepository badgeLevelRepo;
    private final UserAccountRepository userAccountRepo;
    private final CentreRepository centreRepo;
    private final GroupRepository groupRepo;
    private final ModuleRepository moduleRepo;
    private final ChallengeRepository challengeRepo;
    private final ParentLinkRepository parentLinkRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-username}")
    private String adminUsername;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (badgeCategoryRepo.count() > 0) {
            log.info("Seed data already exists, skipping.");
            return;
        }

        log.info("Seeding initial data...");

        // Badge Categories
        BadgeCategory gameMastery = seedCategory("GAME_MASTERY", "Game Mastery", "Skills related to game knowledge and performance");
        BadgeCategory teamwork = seedCategory("TEAMWORK", "Teamwork", "Collaboration and team-based skills");
        BadgeCategory esportsCitizen = seedCategory("ESPORTS_CITIZEN", "Esports Citizen", "Positive behavior and sportsmanship");
        BadgeCategory personalDev = seedCategory("PERSONAL_DEVELOPMENT", "Personal Development", "Self-improvement and growth");
        BadgeCategory digitalSkills = seedCategory("DIGITAL_SKILLS", "Digital Skills", "Technical and digital literacy skills");

        // Badge Levels
        seedLevel("Bronze", 0, 30, 1);
        seedLevel("Silver", 31, 70, 2);
        seedLevel("Gold", 71, 120, 3);
        seedLevel("Platinum", 121, null, 4);

        // Centre
        Centre centre = new Centre();
        centre.setName("Wishaw YMCA");
        centre.setCode("WISHAW");
        centre = centreRepo.save(centre);

        // Groups
        Group group = new Group();
        group.setName("Fortnite Beginners");
        group.setGameName("Fortnite");
        group.setAgeBand("12-16");
        group.setCentre(centre);
        group = groupRepo.save(group);

        // Super Admin
        UserAccount superAdmin = new UserAccount();
        superAdmin.setUsername(adminUsername);
        superAdmin.setPasswordHash(passwordEncoder.encode(adminPassword));
        superAdmin.setDisplayName("Super Admin");
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setCentre(centre);
        userAccountRepo.save(superAdmin);

        // Centre Admin
        UserAccount centreAdmin = new UserAccount();
        centreAdmin.setUsername("centreadmin");
        centreAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
        centreAdmin.setDisplayName("Centre Admin");
        centreAdmin.setRole(Role.CENTRE_ADMIN);
        centreAdmin.setCentre(centre);
        userAccountRepo.save(centreAdmin);

        // Player
        UserAccount player = new UserAccount();
        player.setUsername("player1");
        player.setPasswordHash(passwordEncoder.encode("player123"));
        player.setDisplayName("Test Player");
        player.setRole(Role.PLAYER);
        player.setCentre(centre);
        player.setGroup(group);
        player = userAccountRepo.save(player);

        // Parent
        UserAccount parent = new UserAccount();
        parent.setUsername("parent1");
        parent.setPasswordHash(passwordEncoder.encode("parent123"));
        parent.setDisplayName("Test Parent");
        parent.setRole(Role.PARENT);
        parent.setCentre(centre);
        parent = userAccountRepo.save(parent);

        // Parent Link
        ParentLink link = new ParentLink();
        link.setParentUser(parent);
        link.setPlayerUser(player);
        link.setRelationshipLabel("Parent");
        parentLinkRepo.save(link);

        // Sample Module with Challenges
        Module module = new Module();
        module.setName("Fortnite Basics");
        module.setGameName("Fortnite");
        module.setDescription("Introduction to Fortnite esports fundamentals");
        module.setActive(true);
        module.setApproved(true);
        module.setCreatedBy(superAdmin);
        module = moduleRepo.save(module);

        seedChallenge(module, gameMastery, "Build Battle Basics", "Complete a basic build battle drill", 10, 1);
        seedChallenge(module, teamwork, "Team Communication", "Use callouts effectively in a team match", 10, 2);
        seedChallenge(module, esportsCitizen, "Good Sportsmanship", "Demonstrate fair play in a competitive match", 10, 3);
        seedChallenge(module, personalDev, "Self Review", "Complete a self-assessment of gameplay", 10, 4);
        seedChallenge(module, digitalSkills, "Stream Setup", "Set up a basic streaming environment", 10, 5);

        log.info("Seed data loaded successfully.");
    }

    private BadgeCategory seedCategory(String code, String displayName, String description) {
        BadgeCategory cat = new BadgeCategory();
        cat.setCode(code);
        cat.setDisplayName(displayName);
        cat.setDescription(description);
        return badgeCategoryRepo.save(cat);
    }

    private void seedLevel(String name, int min, Integer max, int rank) {
        BadgeLevel level = new BadgeLevel();
        level.setName(name);
        level.setMinPoints(min);
        level.setMaxPoints(max);
        level.setRankOrder(rank);
        badgeLevelRepo.save(level);
    }

    private void seedChallenge(Module module, BadgeCategory category, String name, String desc, int points, int order) {
        Challenge c = new Challenge();
        c.setModule(module);
        c.setBadgeCategory(category);
        c.setName(name);
        c.setDescription(desc);
        c.setPoints(points);
        c.setDisplayOrder(order);
        challengeRepo.save(c);
    }
}
