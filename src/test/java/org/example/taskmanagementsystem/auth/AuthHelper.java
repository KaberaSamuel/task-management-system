package org.example.taskmanagementsystem.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanagementsystem.enums.UserRole;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class AuthHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public AuthHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    // Register a user
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void registerUser(String username, String email, String password, UserRole role) throws Exception {
        Map<String, Object> registerRequest = Map.of(
                "username", username,
                "email", email,
                "password", password,
                "role", role
        );

        mockMvc.perform(post("/auth/register")
                        .with(user("admin").roles("ADMIN"))     // grant admin authorities
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    // Register a default test user
    public void registerDefaultUser() throws Exception {
        registerUser("testUser", "test@example.com", "test1234", UserRole.MEMBER);
    }

    // Login and return access token
    public String loginAndGetToken(String email, String password) throws Exception {
        Map<String, String> loginRequest = Map.of(
                "email", email,
                "password", password
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("accessToken").asText();
    }
}