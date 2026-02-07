package org.example.taskmanagementsystem.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanagementsystem.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthenticationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper method to register a user
    private void registerUser(String username, String email, String password, UserRole role) throws Exception {
        Map<String, Object> registerRequest = Map.of(
                "username", username,
                "email", email,
                "password", password,
                "role", role
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }


    // Helper method to register a default test user
    private void registerDefaultUser() throws Exception {
        registerUser("testUser", "test@example.com", "test1234", UserRole.MEMBER);
    }

    // Helper method to login and return access token
    private String loginAndGetToken(String email, String password) throws Exception {
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

    @Test
    public void testLoginSuccess() throws Exception {
        // Register user
        registerDefaultUser();

        // Login and get token
        String accessToken = loginAndGetToken("test@example.com", "test1234");

        // Verify token works by accessing protected route
        mockMvc.perform(get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        // Register user
        registerDefaultUser();

        // Attempt login with wrong password
        Map<String, String> loginRequest = Map.of(
                "email", "test@example.com",
                "password", "wrong_password"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        // Attempt login with nonexistent email
        Map<String, String> loginRequest2 = Map.of(
                "email", "random@example.com",
                "password", "test1234"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest2)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        // Register and login
        registerDefaultUser();
        String accessToken = loginAndGetToken("test@example.com", "test1234");

        // Verify token works
        mockMvc.perform(get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Logout
        mockMvc.perform(post("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Verify token no longer works
        mockMvc.perform(get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is(anyOf(is(401), is(403))));
    }

    @Test
    public void testLogoutFailure() throws Exception {
        // attempt logout without authorization header
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is(anyOf(is(401), is(403))));
    }
}