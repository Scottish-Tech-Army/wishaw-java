package org.scottishtecharmy.wishaw.config;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.entity.Module;
import org.scottishtecharmy.wishaw.repository.*;
import org.scottishtecharmy.wishaw.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CentreRepository centreRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final LevelRepository levelRepository;
    private final AgeGroupRepository ageGroupRepository;
    private final SkillRepository skillRepository;
    private final BadgeRepository badgeRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final TeamRepository teamRepository;
    private final ModuleRepository moduleRepository;
    private final PlayerRepository playerRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (centreRepository.count() > 0) return;

        // Centres
        Centre wishaw = new Centre();
        wishaw.setName("Wishaw YMCA Esports Academy");
        wishaw.setLocation("Wishaw, Scotland");
        wishaw.setDescription("Community esports academy for youth aged 8-18.");
        centreRepository.save(wishaw);

        // Roles
        Role adminRole = createRole("centre-admin");
        Role coachRole = createRole("coach");
        Role parentRole = createRole("parent");
        Role playerRole = createRole("player");

        // Levels
        Level bronze = createLevel("Bronze", 0, 30, 1, "Starting level for all players");
        Level silver = createLevel("Silver", 31, 70, 2, "Intermediate level");
        Level gold = createLevel("Gold", 71, 120, 3, "Advanced level");
        Level platinum = createLevel("Platinum", 121, 200, 4, "Expert level");
        Level diamond = createLevel("Diamond", 201, null, 5, "Elite level");

        // Age Groups
        AgeGroup juniors = new AgeGroup();
        juniors.setName("Juniors");
        juniors.setDescription("Age 8-14 junior esports group");
        juniors.setMinAge(8);
        juniors.setMaxAge(14);
        ageGroupRepository.save(juniors);

        AgeGroup seniors = new AgeGroup();
        seniors.setName("Seniors 13+");
        seniors.setDescription("Competitive group for 13+ players");
        seniors.setMinAge(13);
        seniors.setMaxAge(18);
        ageGroupRepository.save(seniors);

        AgeGroup competitive = new AgeGroup();
        competitive.setName("Competitive 16+");
        competitive.setDescription("Advanced competitive group for 16+ players");
        competitive.setMinAge(16);
        competitive.setMaxAge(25);
        ageGroupRepository.save(competitive);

        // Skills (Youthwork Skills and Outcomes Framework)
        Skill teamwork = createSkill("Teamwork");
        Skill communication = createSkill("Communication");
        Skill leadership = createSkill("Leadership");
        Skill problemSolving = createSkill("Problem Solving");
        Skill digitalLiteracy = createSkill("Digital Literacy");
        Skill selfAwareness = createSkill("Self-Awareness");
        Skill resilience = createSkill("Resilience");
        Skill creativity = createSkill("Creativity");
        Skill onlineSafety = createSkill("Online Safety");
        Skill criticalThinking = createSkill("Critical Thinking");

        // Main Badges (5 persistent badges)
        Badge gameMastery = createBadge("Game Mastery", wishaw,
                "Game Mastery involves young gamers learning game mechanics, developing strategies, and making informed decisions during gameplay.");
        Badge teamworkBadge = createBadge("Teamwork", wishaw,
                "Teamwork is when young people work together by sharing goals, supporting each other, and completing tasks to achieve a common outcome.");
        Badge esportsCitizen = createBadge("Esports Citizen", wishaw,
                "Esports citizen is where young people learn how to participate online in a positive way, supporting positive competition and appropriate communication.");
        Badge personalDev = createBadge("Personal Development", wishaw,
                "Personal development is used to improve young people's skills, building confidence and self-awareness.");
        Badge digitalSkills = createBadge("Digital Skills", wishaw,
                "Digital skills involve young people learning how to stay safe online and understanding how to use online tools.");

        // Sub-badges / Challenges for Game Mastery
        SubBadge sb1 = createSubBadge("Basic Controls Mastery", 10, "Learn the fundamental controls and mechanics");
        sb1.getSkills().add(digitalLiteracy);
        sb1.getSkills().add(problemSolving);
        subBadgeRepository.save(sb1);

        SubBadge sb2 = createSubBadge("Strategy Development", 15, "Develop and apply game strategies");
        sb2.getSkills().add(criticalThinking);
        sb2.getSkills().add(problemSolving);

        SubBadge sb3 = createSubBadge("Rank Improvement", 20, "Improve rank through consistent performance");
        sb3.getSkills().add(resilience);
        sb3.getSkills().add(selfAwareness);

        gameMastery.getSubBadges().add(sb1);
        gameMastery.getSubBadges().add(sb2);
        gameMastery.getSubBadges().add(sb3);
        badgeRepository.save(gameMastery);

        // Sub-badges for Teamwork badge
        SubBadge sb4 = createSubBadge("Team Communication", 10, "Communicate effectively with teammates");
        sb4.getSkills().add(communication);
        sb4.getSkills().add(teamwork);

        SubBadge sb5 = createSubBadge("Leadership Role", 15, "Take a leadership role in team activities");
        sb5.getSkills().add(leadership);
        sb5.getSkills().add(teamwork);

        teamworkBadge.getSubBadges().add(sb4);
        teamworkBadge.getSubBadges().add(sb5);
        badgeRepository.save(teamworkBadge);

        // Sub-badges for Esports Citizen
        SubBadge sb6 = createSubBadge("Code of Conduct", 10, "Create and follow a code of conduct");
        sb6.getSkills().add(communication);
        sb6.getSkills().add(selfAwareness);

        SubBadge sb7 = createSubBadge("Positive Online Behaviour", 10, "Demonstrate positive online behaviour");
        sb7.getSkills().add(onlineSafety);
        sb7.getSkills().add(communication);

        esportsCitizen.getSubBadges().add(sb6);
        esportsCitizen.getSubBadges().add(sb7);
        badgeRepository.save(esportsCitizen);

        // Sub-badges for Personal Development
        SubBadge sb8 = createSubBadge("Goal Setting", 10, "Set and review personal goals");
        sb8.getSkills().add(selfAwareness);
        sb8.getSkills().add(resilience);

        SubBadge sb9 = createSubBadge("Reflection & Improvement", 15, "Reflect on performance and identify improvements");
        sb9.getSkills().add(selfAwareness);
        sb9.getSkills().add(criticalThinking);

        personalDev.getSubBadges().add(sb8);
        personalDev.getSubBadges().add(sb9);
        badgeRepository.save(personalDev);

        // Sub-badges for Digital Skills
        SubBadge sb10 = createSubBadge("Online Safety", 10, "Demonstrate knowledge of online safety");
        sb10.getSkills().add(onlineSafety);
        sb10.getSkills().add(digitalLiteracy);

        SubBadge sb11 = createSubBadge("Digital Creation", 15, "Create digital content using available tools");
        sb11.getSkills().add(creativity);
        sb11.getSkills().add(digitalLiteracy);

        digitalSkills.getSubBadges().add(sb10);
        digitalSkills.getSubBadges().add(sb11);
        badgeRepository.save(digitalSkills);

        // Teams
        Team minecraftTeam = new Team();
        minecraftTeam.setCentre(wishaw);
        minecraftTeam.setTeamName("Minecraft Juniors");
        minecraftTeam.setDescription("Minecraft group for junior players");
        teamRepository.save(minecraftTeam);

        Team rocketLeagueTeam = new Team();
        rocketLeagueTeam.setCentre(wishaw);
        rocketLeagueTeam.setTeamName("Rocket League Group");
        rocketLeagueTeam.setDescription("Rocket league group for junior players");
        teamRepository.save(rocketLeagueTeam);

        Team fortniteTeam = new Team();
        fortniteTeam.setCentre(wishaw);
        fortniteTeam.setTeamName("Fortnite Group");
        fortniteTeam.setDescription("Fortnite group for junior players");
        teamRepository.save(fortniteTeam);

        // Modules
        Module minecraftModule = new Module();
        minecraftModule.setName("Defeat the Ender Dragon");
        minecraftModule.setDescription("Work through challenges to defeat the Ender Dragon in Minecraft");
        minecraftModule.setGameType("Minecraft");
        minecraftModule.setCentre(wishaw);

        ModuleDetail md1 = new ModuleDetail();
        md1.setName("Introduction & Setup");
        md1.setWeekNo(1);
        md1.setSessionFocusDescription("Introduction to Minecraft, account setup, and basic controls");
        md1.setSubBadge(sb1);

        ModuleDetail md2 = new ModuleDetail();
        md2.setName("Survival Skills");
        md2.setWeekNo(2);
        md2.setSessionFocusDescription("Learn survival mechanics: gathering resources, crafting basic tools");

        ModuleDetail md3 = new ModuleDetail();
        md3.setName("Building Fundamentals");
        md3.setWeekNo(3);
        md3.setSessionFocusDescription("Build a shelter and learn basic building concepts");

        minecraftModule.getModuleDetails().add(md1);
        minecraftModule.getModuleDetails().add(md2);
        minecraftModule.getModuleDetails().add(md3);
        moduleRepository.save(minecraftModule);

        Module rocketLeagueModule = new Module();
        rocketLeagueModule.setName("Road to Diamond - Rocket League");
        rocketLeagueModule.setDescription("Develop Rocket League skills from beginner to advanced level");
        rocketLeagueModule.setGameType("Rocket League");
        rocketLeagueModule.setCentre(wishaw);

        ModuleDetail rl1 = new ModuleDetail();
        rl1.setName("Car Control Basics");
        rl1.setWeekNo(1);
        rl1.setSessionFocusDescription("Master basic car control and boost management");

        ModuleDetail rl2 = new ModuleDetail();
        rl2.setName("Aerial Basics");
        rl2.setWeekNo(2);
        rl2.setSessionFocusDescription("Learn aerial mechanics and wall play");

        rocketLeagueModule.getModuleDetails().add(rl1);
        rocketLeagueModule.getModuleDetails().add(rl2);
        moduleRepository.save(rocketLeagueModule);

        // Admin user
        User admin = new User();
        admin.setFirstName("Centre");
        admin.setLastName("Admin");
        admin.setUsername("admin@wishaw.ymca");
        admin.setPassword("Admin@1234");
        admin.setEnabled(true);
        userService.save(admin);
        userService.assignRole(admin, wishaw, "centre-admin");

        // Coach user
        User coach = new User();
        coach.setFirstName("Coach");
        coach.setLastName("Emma");
        coach.setUsername("coach@wishaw.ymca");
        coach.setPassword("Coach@1234");
        coach.setEnabled(true);
        userService.save(coach);
        userService.assignRole(coach, wishaw, "coach");

        // Sample player user
        User playerUser = new User();
        playerUser.setFirstName("Alex");
        playerUser.setLastName("Smith");
        playerUser.setUsername("player@wishaw.ymca");
        playerUser.setPassword("Player@1234");
        playerUser.setEnabled(true);
        userService.save(playerUser);
        userService.assignRole(playerUser, wishaw, "player");

        // Sample parent user
        User parentUser = new User();
        parentUser.setFirstName("Sarah");
        parentUser.setLastName("Smith");
        parentUser.setUsername("parent@wishaw.ymca");
        parentUser.setPassword("Parent@1234");
        parentUser.setEnabled(true);
        userService.save(parentUser);
        userService.assignRole(parentUser, wishaw, "parent");

        // Player record
        Player player = new Player();
        player.setCentre(wishaw);
        player.setUser(playerUser);
        player.setParent(parentUser);
        player.setAgeGroup(juniors);
        player.setCurrentModule(minecraftModule);
        player.setTeam(minecraftTeam);
        player.setTotalXp(0);
        playerRepository.save(player);
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private Level createLevel(String name, int min, Integer max, int order, String description) {
        Level level = new Level();
        level.setName(name);
        level.setMinPoints(min);
        level.setMaxPoints(max);
        level.setDisplayOrder(order);
        level.setDescription(description);
        return levelRepository.save(level);
    }

    private Skill createSkill(String name) {
        Skill skill = new Skill();
        skill.setName(name);
        return skillRepository.save(skill);
    }

    private Badge createBadge(String name, Centre centre, String description) {
        Badge badge = new Badge();
        badge.setName(name);
        badge.setCentre(centre);
        badge.setDescription(description);
        return badgeRepository.save(badge);
    }

    private SubBadge createSubBadge(String name, int points, String description) {
        SubBadge sb = new SubBadge();
        sb.setName(name);
        sb.setPoint(points);
        sb.setDescription(description);
        return subBadgeRepository.save(sb);
    }
}
