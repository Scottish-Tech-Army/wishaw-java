package org.scottishtecharmy.wishaw_java.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginReturnsJwtAndUserPayload() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@wymca.org",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.user.id").value("u1"))
                .andExpect(jsonPath("$.profile.displayName").value("Emma W"));
    }

    @Test
    void meReturnsCurrentSessionWhenTokenProvided() throws Exception {
        String loginBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "player1@wymca.org",
                                  "password": "player123"
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginBody);
        String accessToken = loginJson.get("accessToken").asText();

        String meBody = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value("u2"))
                .andExpect(jsonPath("$.profile.displayName").value("Player One"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode meJson = objectMapper.readTree(meBody);
        assertThat(meJson.get("user").get("email").asText()).isEqualTo("player1@wymca.org");
    }

      @Test
      void registerCreatesNewPlayerAndReturnsSessionPayload() throws Exception {
        String registerBody = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "newplayer@example.com",
                      "password": "player123",
                      "displayName": "New Player",
                      "firstName": "New",
                                  "lastName": "Player",
                                  "dateOfBirth": "2010-01-15"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isString())
            .andExpect(jsonPath("$.refreshToken").isString())
            .andExpect(jsonPath("$.user.email").value("newplayer@example.com"))
            .andExpect(jsonPath("$.user.role").value("PLAYER"))
                .andExpect(jsonPath("$.profile.displayName").value("New Player"))
                .andExpect(jsonPath("$.profile.dateOfBirth").value("2010-01-15"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode registerJson = objectMapper.readTree(registerBody);
        assertThat(registerJson.get("user").get("id").asText()).startsWith("u");
      }
}
