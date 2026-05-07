package org.scottishtecharmy.wishaw_java.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scottishtecharmy.wishaw_java.entity.Sport;
import org.scottishtecharmy.wishaw_java.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TournamentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SportRepository sportRepository;

    @Test
    void joinRejectsPlayersOutsideConfiguredAgeRange() throws Exception {
        String loginBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "player2@wymca.org",
                                  "password": "player123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginBody);
        String accessToken = loginJson.get("accessToken").asText();

        Sport sport = sportRepository.findById("s2").orElseThrow();
        Integer originalMinAge = sport.getMinAge();
        Integer originalMaxAge = sport.getMaxAge();

        try {
            sport.setMinAge(18);
            sport.setMaxAge(25);
            sportRepository.save(sport);

            mockMvc.perform(post("/api/tournaments/t2/join")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", containsString("only available for players aged 18 to 25")));
        } finally {
            sport.setMinAge(originalMinAge);
            sport.setMaxAge(originalMaxAge);
            sportRepository.save(sport);
        }
    }
}