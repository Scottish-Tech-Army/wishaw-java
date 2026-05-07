package org.scottishtecharmy.wishaw_java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scottishtecharmy.wishaw_java.dto.request.AwardChallengeRequest;
import org.scottishtecharmy.wishaw_java.dto.request.SetLegacyPointsRequest;
import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeAwardRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.service.player.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BackendScaffoldIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BadgeCategoryRepository badgeCategoryRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeAwardRepository challengeAwardRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void loginSucceedsForSeededUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "superadmin",
                                "password", "admin123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("superadmin"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));
    }

    @Test
    void loginFailsForInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "superadmin",
                                "password", "wrong-password"
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("AUTHENTICATION_FAILED"));
    }

    @Test
    void h2ConsoleIsNotPubliclyExposedByDefault() throws Exception {
        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void legacyPointsUpdateRecalculatesLevel() {
        UserAccount player = getPlayer("player1");
        BadgeCategory category = getCategory("GAME_MASTERY");

        SetLegacyPointsRequest request = new SetLegacyPointsRequest();
        request.setPlayerId(player.getId());
        request.setBadgeCategoryId(category.getId());
        request.setLegacyPoints(35);

        BadgeProgressResponse response = progressService.setLegacyPoints(request);

        assertThat(response.getLegacyPoints()).isEqualTo(35);
        assertThat(response.getTotalPoints()).isEqualTo(35);
        assertThat(response.getCurrentLevel()).isEqualTo("Silver");
    }

    @Test
    void challengeAwardFlowAddsEarnedPoints() {
        UserAccount player = getPlayer("player1");
        Challenge challenge = challengeRepository.findAll().stream()
                .filter(item -> "TEAMWORK".equals(item.getBadgeCategory().getCode()))
                .findFirst()
                .orElseThrow();

        AwardChallengeRequest request = new AwardChallengeRequest();
        request.setPlayerId(player.getId());
        request.setChallengeId(challenge.getId());
        request.setNotes("Test award");

        long awardsBefore = challengeAwardRepository.count();
        BadgeProgressResponse response = progressService.awardChallenge(request, "superadmin");

        assertThat(challengeAwardRepository.count()).isEqualTo(awardsBefore + 1);
        assertThat(response.getCategoryCode()).isEqualTo("TEAMWORK");
        assertThat(response.getEarnedPoints()).isEqualTo(challenge.getPoints());
        assertThat(response.getTotalPoints()).isEqualTo(challenge.getPoints());
    }

    @Test
    void centreAdminCannotAccessOrAwardPlayersOutsideTheirCentre() throws Exception {
        UserAccount remotePlayer = createPlayerInOtherCentre("remoteplayer", "Remote Player");
        Challenge challenge = challengeRepository.findAll().stream()
                .filter(item -> "TEAMWORK".equals(item.getBadgeCategory().getCode()))
                .findFirst()
                .orElseThrow();
        MockHttpSession centreAdminSession = login("centreadmin", "admin123");

        mockMvc.perform(get("/api/v1/players/{playerId}/profile", remotePlayer.getId())
                        .session(centreAdminSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/players/{playerId}/progress", remotePlayer.getId())
                        .session(centreAdminSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));

        long awardsBefore = challengeAwardRepository.count();
        mockMvc.perform(post("/api/v1/admin/progress/award-challenge")
                        .session(centreAdminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "playerId", remotePlayer.getId(),
                                "challengeId", challenge.getId(),
                                "notes", "Cross-centre award should fail"
                        ))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
        assertThat(challengeAwardRepository.count()).isEqualTo(awardsBefore);
    }

    @Test
    void centreAdminCanReadPlayersInTheirOwnCentre() throws Exception {
        MockHttpSession centreAdminSession = login("centreadmin", "admin123");
        UserAccount player = getPlayer("player1");

        mockMvc.perform(get("/api/v1/players/{playerId}/profile", player.getId())
                        .session(centreAdminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("player1"));

        mockMvc.perform(get("/api/v1/players/{playerId}/progress", player.getId())
                        .session(centreAdminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void recalculatePlayerProgressCombinesLegacyAndAwards() {
        UserAccount player = getPlayer("player1");
        BadgeCategory category = getCategory("TEAMWORK");
        Challenge challenge = challengeRepository.findAll().stream()
                .filter(item -> category.getId().equals(item.getBadgeCategory().getId()))
                .findFirst()
                .orElseThrow();

        SetLegacyPointsRequest legacyRequest = new SetLegacyPointsRequest();
        legacyRequest.setPlayerId(player.getId());
        legacyRequest.setBadgeCategoryId(category.getId());
        legacyRequest.setLegacyPoints(35);
        progressService.setLegacyPoints(legacyRequest);

        AwardChallengeRequest awardRequest = new AwardChallengeRequest();
        awardRequest.setPlayerId(player.getId());
        awardRequest.setChallengeId(challenge.getId());
        progressService.awardChallenge(awardRequest, "superadmin");

        progressService.recalculatePlayerProgress(player.getId());
        BadgeProgressResponse progress = progressService.getPlayerProgress(player.getId()).stream()
                .filter(item -> "TEAMWORK".equals(item.getCategoryCode()))
                .findFirst()
                .orElseThrow();

        assertThat(progress.getLegacyPoints()).isEqualTo(35);
        assertThat(progress.getEarnedPoints()).isEqualTo(challenge.getPoints());
        assertThat(progress.getTotalPoints()).isEqualTo(35 + challenge.getPoints());
        assertThat(progress.getCurrentLevel()).isEqualTo("Silver");
    }

    @Test
    void parentCanOnlyReadLinkedPlayerProfiles() throws Exception {
        UserAccount unrelatedPlayer = new UserAccount();
        unrelatedPlayer.setUsername("otherplayer");
        unrelatedPlayer.setPasswordHash(passwordEncoder.encode("other123"));
        unrelatedPlayer.setDisplayName("Other Player");
        unrelatedPlayer.setRole(Role.PLAYER);
        unrelatedPlayer.setCentre(getPlayer("player1").getCentre());
        unrelatedPlayer = userAccountRepository.save(unrelatedPlayer);

        MockHttpSession parentSession = login("parent1", "parent123");
        Long linkedPlayerId = getPlayer("player1").getId();

        mockMvc.perform(get("/api/v1/parent/players/{playerId}/profile", linkedPlayerId)
                        .session(parentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("player1"));

        mockMvc.perform(get("/api/v1/parent/players/{playerId}/profile", unrelatedPlayer.getId())
                        .session(parentSession))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void csvPreviewParsesRowsAndFlagsUnmappedPlayers() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        String csv = "username,badgeCategoryCode,legacyPoints,challengePoints\n"
                + "player1,GAME_MASTERY,20,5\n"
                + "missingUser,TEAMWORK,10,0\n";

        mockMvc.perform(multipart("/api/v1/admin/import/csv/upload")
                        .file("file", csv.getBytes(StandardCharsets.UTF_8))
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(2))
                .andExpect(jsonPath("$.validRows").value(1))
                .andExpect(jsonPath("$.warningRows").value(1))
                .andExpect(jsonPath("$.unmappedPlayers[0]").value("missingUser"));
    }

    @Test
    void csvCommitIsIdempotent() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        String csv = "username,badgeCategoryCode,legacyPoints,challengePoints,sourceReference\n"
                + "player1,ESPORTS_CITIZEN,0,8,import-row-1\n";

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/admin/import/csv/upload")
                        .file("file", csv.getBytes(StandardCharsets.UTF_8))
                        .session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode uploadJson = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long batchId = uploadJson.get("batchId").asLong();
        long awardsBefore = challengeAwardRepository.count();

        mockMvc.perform(post("/api/v1/admin/import/{batchId}/commit", batchId)
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAwardsCreated").value(1))
                .andExpect(jsonPath("$.status").value("COMMITTED"));

        mockMvc.perform(post("/api/v1/admin/import/{batchId}/commit", batchId)
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAwardsCreated").value(0))
                .andExpect(jsonPath("$.status").value("COMMITTED"));

        assertThat(challengeAwardRepository.count()).isEqualTo(awardsBefore + 1);
    }

    @Test
    void csvCommitRejectsUnmappedPlayersAndDoesNotAutoCreateAccounts() throws Exception {
        MockHttpSession adminSession = login("superadmin", "admin123");
        String csv = "username,badgeCategoryCode,legacyPoints,challengePoints\n"
                + "missingUser,TEAMWORK,10,0\n";

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/admin/import/csv/upload")
                        .file("file", csv.getBytes(StandardCharsets.UTF_8))
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warningRows").value(1))
                .andReturn();

        long batchId = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("batchId").asLong();
        long awardsBefore = challengeAwardRepository.count();

        assertThat(userAccountRepository.findByUsername("missingUser")).isEmpty();

        mockMvc.perform(post("/api/v1/admin/import/{batchId}/commit", batchId)
                        .session(adminSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("All import rows must be mapped to existing player accounts before commit"));

        assertThat(userAccountRepository.findByUsername("missingUser")).isEmpty();
        assertThat(challengeAwardRepository.count()).isEqualTo(awardsBefore);
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

    private UserAccount getPlayer(String username) {
        return userAccountRepository.findByUsername(username).orElseThrow();
    }

    private BadgeCategory getCategory(String code) {
        return badgeCategoryRepository.findByCode(code).orElseThrow();
    }

    private UserAccount createPlayerInOtherCentre(String username, String displayName) {
        Centre centre = new Centre();
        centre.setName("Other Centre");
        centre.setCode("OTHER-" + username.toUpperCase());
        centre = centreRepository.save(centre);

        UserAccount player = new UserAccount();
        player.setUsername(username);
        player.setPasswordHash(passwordEncoder.encode("player123"));
        player.setDisplayName(displayName);
        player.setRole(Role.PLAYER);
        player.setCentre(centre);
        return userAccountRepository.save(player);
    }
}
