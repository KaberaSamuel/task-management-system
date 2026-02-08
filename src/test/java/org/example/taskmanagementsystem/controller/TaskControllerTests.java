package org.example.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanagementsystem.dto.task.CreateTaskDTO;
import org.example.taskmanagementsystem.dto.task.GetTaskDTO;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldGetAllTasks() throws Exception {
        GetTaskDTO task1 = new GetTaskDTO("Task 1", "Description 1", "TODO", "HIGH", "sam@gmail.com");
        GetTaskDTO task2 = new GetTaskDTO("Task 2", "Description 2", "IN_PROGRESS", "MEDIUM", "test@gmail.com");
        List<GetTaskDTO> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[0].ownerEmail").value("sam@gmail.com"))
                .andExpect(jsonPath("$[1].ownerEmail").value("test@gmail.com"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        Long taskId = 1L;
        GetTaskDTO task = new GetTaskDTO("Task 1", "Description 1", "TODO", "HIGH", "sam@gmail.com");

        Mockito.when(taskService.getTaskById(taskId)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.ownerEmail").value("sam@gmail.com"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        Long taskId = 1L;
        Mockito.when(taskService.getTaskById(taskId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldCreateTask() throws Exception {
        CreateTaskDTO inputTask = new CreateTaskDTO("New Task", "Description", "TODO", "HIGH");
        GetTaskDTO createdTask = new GetTaskDTO("New Task", "Description", "TODO", "HIGH", "sam@gmail.com");

        Mockito.when(taskService.createTask(any(CreateTaskDTO.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.ownerEmail").value("sam@gmail.com"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenCreatingTaskWithInvalidOwner() throws Exception {
        CreateTaskDTO inputTask = new CreateTaskDTO("New Task", "Description", "TODO", "HIGH");
        String errorMessage = "User with email: invalid@gmail.com not found";

        Mockito.when(taskService.createTask(any(CreateTaskDTO.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTask)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateTask() throws Exception {
        Long taskId = 1L;
        CreateTaskDTO updateInfo = new CreateTaskDTO("Updated Task", "Updated Description", "DONE", "LOW");
        GetTaskDTO updatedTask = new GetTaskDTO("Updated Task", "Updated Description", "DONE", "LOW", "sam@gmail.com");

        Mockito.when(taskService.updateTask(eq(taskId), any(CreateTaskDTO.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @WithMockUser
    void shouldDeleteTask() throws Exception {
        Long taskId = 1L;

        Mockito.doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }
}
