package org.example.taskmanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.taskmanagementsystem.auth.AuthHelper;
import org.example.taskmanagementsystem.dto.task.CreateTaskDTO;
import org.example.taskmanagementsystem.dto.task.GetTaskDTO;
import org.example.taskmanagementsystem.enums.TaskPriority;
import org.example.taskmanagementsystem.enums.TaskStatus;
import org.example.taskmanagementsystem.enums.UserRole;
import org.example.taskmanagementsystem.dto.OwnerTaskCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TasksAuthorizationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthHelper authHelper;

    private OwnerTaskCredentials createTaskAsOwner() throws Exception {
        // Register and login as owner
        authHelper.registerUser("owner", "owner@example.com", "password", UserRole.MEMBER);
        String ownerToken = authHelper.loginAndGetToken("owner@example.com", "password");

        // Create task as owner
        CreateTaskDTO createTask = new CreateTaskDTO("My Task", "Description", TaskStatus.TODO, TaskPriority.HIGH);
        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTask)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        GetTaskDTO createdTask = objectMapper.readValue(responseBody, GetTaskDTO.class);

        return new OwnerTaskCredentials(createdTask.getId(), ownerToken);
    }

    @Test
    public void ownerShouldUpdateOwnTask() throws Exception {
        // Create task as owner
        OwnerTaskCredentials ownerTaskCredentials = createTaskAsOwner();

        // Update task as owner - should succeed
        CreateTaskDTO updateTask = new CreateTaskDTO("Updated Task", "New Description", TaskStatus.COMPLETED, TaskPriority.LOW);
        mockMvc.perform(put("/api/tasks/{id}", ownerTaskCredentials.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerTaskCredentials.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    public void nonOwnerShouldNotUpdateTask() throws Exception {
        // Create task as owner
        OwnerTaskCredentials ownerTaskCredentials = createTaskAsOwner();

        // Register different user
        authHelper.registerUser("otherUser", "other@example.com", "password", UserRole.MEMBER);
        String otherToken = authHelper.loginAndGetToken("other@example.com", "password");

        // Try to update as non-owner - should fail
        CreateTaskDTO updateTask = new CreateTaskDTO("Hacked Task", "Evil Description", TaskStatus.COMPLETED, TaskPriority.LOW);
        mockMvc.perform(put("/api/tasks/{id}", ownerTaskCredentials.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void adminShouldUpdateAnyTask() throws Exception {
        // Create task as regular user
        OwnerTaskCredentials ownerTaskCredentials = createTaskAsOwner();

        // Register admin
        authHelper.registerUser("admin", "admin@example.com", "password", UserRole.ADMIN);
        String adminToken = authHelper.loginAndGetToken("admin@example.com", "password");

        // Update as admin - should succeed
        CreateTaskDTO updateTask = new CreateTaskDTO("Admin Updated", "Admin Description", TaskStatus.COMPLETED, TaskPriority.LOW);
        mockMvc.perform(put("/api/tasks/{id}", ownerTaskCredentials.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Updated"));
    }

    @Test
    public void ownerShouldDeleteOwnTask() throws Exception {
        // Create task as owner
        OwnerTaskCredentials ownerTaskCredentials = createTaskAsOwner();

        // Delete task as owner - should succeed
        mockMvc.perform(delete("/api/tasks/{id}", ownerTaskCredentials.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerTaskCredentials.token())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    @Test
    public void nonOwnerShouldNotDeleteTask() throws Exception {
        // Create task as owner
        OwnerTaskCredentials ownerTaskCredentials = createTaskAsOwner();

        // Register different user
        authHelper.registerUser("otherUser", "other@example.com", "password", UserRole.MEMBER);
        String otherToken = authHelper.loginAndGetToken("other@example.com", "password");

        // Delete task as a non owner - should fail
        mockMvc.perform(delete("/api/tasks/{id}", ownerTaskCredentials.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void adminShouldDeleteAnyTask() throws Exception {
        // Create task as regular user
        OwnerTaskCredentials ownerTaskCredentials = createTaskAsOwner();

        // Register admin
        authHelper.registerUser("admin", "admin@example.com", "password", UserRole.ADMIN);
        String adminToken = authHelper.loginAndGetToken("admin@example.com", "password");

        // Delete as admin - should succeed
        mockMvc.perform(delete("/api/tasks/{id}", ownerTaskCredentials.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}