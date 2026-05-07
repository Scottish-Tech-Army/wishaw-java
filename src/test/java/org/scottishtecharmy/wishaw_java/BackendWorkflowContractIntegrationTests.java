package org.scottishtecharmy.wishaw_java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BackendWorkflowContractIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BadgeCategoryRepository badgeCategoryRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

        @Autowired
        private ModuleRepository moduleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void authMeAndLogoutFollowSessionContract() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");

        mockMvc.perform(get("/api/v1/auth/me").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("superadmin"))
                .andExpect(jsonPath("$.displayName").value("Super Admin"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));

        mockMvc.perform(post("/api/v1/auth/logout").session(adminSession))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/auth/me").session(adminSession))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanSetUpCentreGroupAndPlayerAccounts() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");

        MvcResult centreResult = mockMvc.perform(post("/api/v1/admin/centres")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Glasgow YMCA",
                                "code", "GLA"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Glasgow YMCA"))
                .andReturn();
        long centreId = readId(centreResult);

        MvcResult groupResult = mockMvc.perform(post("/api/v1/admin/groups")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Rocket League Squad",
                                "gameName", "Rocket League",
                                "ageBand", "13-17",
                                "centreId", centreId
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.centreId").value(centreId))
                .andReturn();
        long groupId = readId(groupResult);

        MvcResult userResult = mockMvc.perform(post("/api/v1/admin/users")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "newplayer",
                                "password", "player123",
                                "displayName", "New Player",
                                "role", "PLAYER",
                                "centreId", centreId,
                                "groupId", groupId,
                                "externalRef", "legacy-newplayer"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newplayer"))
                .andExpect(jsonPath("$.centreId").value(centreId))
                .andExpect(jsonPath("$.groupId").value(groupId))
                .andReturn();
        long userId = readId(userResult);

        mockMvc.perform(get("/api/v1/admin/users/{id}", userId).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("New Player"))
                .andExpect(jsonPath("$.role").value("PLAYER"))
                .andExpect(jsonPath("$.centreName").value("Glasgow YMCA"))
                .andExpect(jsonPath("$.groupName").value("Rocket League Squad"));

        mockMvc.perform(patch("/api/v1/admin/users/{id}/status", userId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void adminCanCreateModuleChallengeAndScheduleForBadgeJourney() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        BadgeCategory category = getCategory("GAME_MASTERY");

        MvcResult moduleResult = mockMvc.perform(post("/api/v1/admin/modules")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Valorant Foundations",
                                "gameName", "Valorant",
                                "description", "Core mechanics for junior players"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Valorant Foundations"))
                .andReturn();
        long moduleId = readId(moduleResult);

        MvcResult challengeResult = mockMvc.perform(post("/api/v1/admin/modules/{id}/challenges", moduleId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Crosshair Placement",
                                "description", "Hold angles correctly in a practice round",
                                "badgeCategoryId", category.getId(),
                                "points", 15,
                                "displayOrder", 1
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.challenges.length()").value(1))
                .andReturn();
        long challengeId = objectMapper.readTree(challengeResult.getResponse().getContentAsString())
                .get("challenges").get(0).get("id").asLong();

        mockMvc.perform(post("/api/v1/admin/modules/{id}/schedule-items", moduleId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "weekNumber", 1,
                                "sessionFocus", "Aim and positioning",
                                "linkedChallengeId", challengeId,
                                "sessionPlanUrl", "https://example.org/plan",
                                "sessionSlidesUrl", "https://example.org/slides",
                                "displayOrder", 1
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.challenges.length()").value(1))
                .andExpect(jsonPath("$.scheduleItems.length()").value(1))
                .andExpect(jsonPath("$.scheduleItems[0].linkedChallengeId").value(challengeId));

        mockMvc.perform(get("/api/v1/admin/modules/{id}", moduleId).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valorant Foundations"))
                .andExpect(jsonPath("$.challenges[0].name").value("Crosshair Placement"))
                .andExpect(jsonPath("$.scheduleItems[0].sessionFocus").value("Aim and positioning"));
    }

    @Test
    void validationAndAuthorizationContractsProtectAdminWorkflow() throws Exception {
        MockHttpSession playerSession = login("player1", "player123");
        mockMvc.perform(get("/api/v1/admin/users").session(playerSession))
                .andExpect(status().isForbidden());

        MockHttpSession adminSession = login("superadmin", "admin123");
        mockMvc.perform(post("/api/v1/admin/centres")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "",
                                "code", ""
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/api/v1/admin/centres"));
    }

    @Test
    void adminCanCreateAndUpdateEnrollmentStatus() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        UserAccount player = getUser("player1");
        var module = moduleRepository.findAll().stream().findFirst().orElseThrow();

        MvcResult enrollmentResult = mockMvc.perform(post("/api/v1/admin/enrollments")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "playerId", player.getId(),
                                "moduleId", module.getId(),
                                "groupId", player.getGroup().getId()
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerId").value(player.getId()))
                .andExpect(jsonPath("$.moduleId").value(module.getId()))
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andReturn();
        long enrollmentId = readId(enrollmentResult);

        mockMvc.perform(patch("/api/v1/admin/enrollments/{id}/status", enrollmentId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "COMPLETED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void playerAndParentViewsExposeProgressAndLeaderboardData() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        UserAccount player = getUser("player1");
        Challenge challenge = challengeRepository.findAll().stream()
                .filter(item -> "GAME_MASTERY".equals(item.getBadgeCategory().getCode()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/api/v1/admin/progress/award-challenge")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "playerId", player.getId(),
                                "challengeId", challenge.getId(),
                                "notes", "Awarded from admin dashboard test"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryCode").value("GAME_MASTERY"))
                .andExpect(jsonPath("$.totalPoints").value(challenge.getPoints()));

        MockHttpSession playerSession = login("player1", "player123");
        mockMvc.perform(get("/api/v1/me/profile").session(playerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("player1"))
                .andExpect(jsonPath("$.badgeProgress.length()").value(5))
                .andExpect(jsonPath("$.overallTotalPoints").value(challenge.getPoints()));

        mockMvc.perform(get("/api/v1/leaderboards/global").session(playerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].displayName").value("Test Player"))
                .andExpect(jsonPath("$[0].totalPoints").value(challenge.getPoints()));

        mockMvc.perform(get("/api/v1/leaderboards/centre/{centreId}", player.getCentre().getId()).session(playerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerId").value(player.getId()));

        mockMvc.perform(get("/api/v1/leaderboards/group/{groupId}", player.getGroup().getId()).session(playerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerId").value(player.getId()));

        MockHttpSession parentSession = login("parent1", "parent123");
        mockMvc.perform(get("/api/v1/parent/players").session(parentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("player1"));
    }

    @Test
    void importMappingPreviewAndReportSupportWizardFlow() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        UserAccount player = getUser("player1");

        String csv = "username,badgeCategoryCode,legacyPoints,challengePoints\n"
                + "mysteryUser,GAME_MASTERY,12,0\n";

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/admin/import/csv/upload")
                        .file("file", csv.getBytes(StandardCharsets.UTF_8))
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warningRows").value(1))
                .andReturn();
        long batchId = readLongField(uploadResult, "batchId");

        mockMvc.perform(post("/api/v1/admin/import/{batchId}/map-players", batchId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "playerMappings", Map.of("mysteryUser", player.getId())
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(batchId))
                .andExpect(jsonPath("$.unmappedPlayers.length()").value(0));

        mockMvc.perform(get("/api/v1/admin/import/{batchId}/preview", batchId).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(batchId))
                .andExpect(jsonPath("$.unmappedPlayers.length()").value(0));

        mockMvc.perform(get("/api/v1/admin/import/{batchId}/report", batchId).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(batchId))
                .andExpect(jsonPath("$.rows.length()").value(1));

        mockMvc.perform(post("/api/v1/admin/import/{batchId}/commit", batchId)
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(batchId))
                .andExpect(jsonPath("$.status").value("COMMITTED"))
                .andExpect(jsonPath("$.totalPlayersAffected").value(1));
    }

    private MockHttpSession login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private long readId(MvcResult result) throws Exception {
                return readLongField(result, "id");
        }

        private long readLongField(MvcResult result, String fieldName) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
                return json.get(fieldName).asLong();
    }

    private BadgeCategory getCategory(String code) {
        return badgeCategoryRepository.findByCode(code).orElseThrow();
    }

    private UserAccount getUser(String username) {
        return userAccountRepository.findByUsername(username).orElseThrow();
    }
}
