package org.example.taskmanagementsystem.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanagementsystem.enums.UserRole;
import org.example.taskmanagementsystem.exception.DuplicateEmailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RegistrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSuccessfulRegistration() throws Exception {
        Map<String, Object> inputUser = Map.of(
                "username", "testUser",
                "email", "test@example.com",
                "password", "test1234",
                "role", UserRole.MEMBER
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testDuplicateEmailException() throws Exception {
        Map<String, Object> inputUser = Map.of(
                "username", "testUser2",
                "email", "test2@example.com",
                "password", "test1234",
                "role", UserRole.MEMBER
        );

        // first registration (should succeed)
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated());

        // second registration should throw duplicate email exception
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    Assertions.assertInstanceOf(DuplicateEmailException.class, result.getResolvedException());
                });
    }

}
