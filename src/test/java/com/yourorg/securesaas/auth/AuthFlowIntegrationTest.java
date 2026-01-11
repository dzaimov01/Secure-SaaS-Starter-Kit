package com.yourorg.securesaas.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.securesaas.infra.db.AuditLogRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private AuditLogRepository auditLogRepository;

  @Test
  void registerCreatesWorkspaceAndAuditLog() throws Exception {
    String registerPayload =
        "{\"email\":\"owner@example.com\",\"password\":\"SuperSecurePass123!\",\"workspaceName\":\"Acme\"}";

    String body =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerPayload))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode json = objectMapper.readTree(body);
    String accessToken = json.get("accessToken").asText();

    mockMvc
        .perform(get("/workspaces").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk());

    assertThat(auditLogRepository.count()).isGreaterThan(0);
  }

  @Test
  void rbacBlocksCrossWorkspaceAccess() throws Exception {
    String registerPayload =
        "{\"email\":\"user1@example.com\",\"password\":\"SuperSecurePass123!\",\"workspaceName\":\"Alpha\"}";

    String body =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerPayload))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode json = objectMapper.readTree(body);
    String accessToken = json.get("accessToken").asText();

    String workspacesBody =
        mockMvc
            .perform(get("/workspaces").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID workspaceId =
        UUID.fromString(objectMapper.readTree(workspacesBody).get(0).get("id").asText());

    mockMvc
        .perform(
            post("/projects")
                .param("workspaceId", workspaceId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Project\",\"description\":\"Test\"}")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk());

    String otherRegister =
        "{\"email\":\"user2@example.com\",\"password\":\"SuperSecurePass123!\",\"workspaceName\":\"Beta\"}";
    String otherBody =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(otherRegister))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String otherToken = objectMapper.readTree(otherBody).get("accessToken").asText();

    mockMvc
        .perform(
            get("/projects")
                .param("workspaceId", workspaceId.toString())
                .header("Authorization", "Bearer " + otherToken))
        .andExpect(status().isForbidden());
  }
}
