package org.example.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanagementsystem.dto.TaskDTO;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldGetAllTasks() throws Exception {
        TaskDTO task1 = new TaskDTO("Task 1", "Description 1", "TODO", "HIGH", "sam@gmail.com", "sam");
        TaskDTO task2 = new TaskDTO("Task 2", "Description 2", "IN_PROGRESS", "MEDIUM", "test@gmail.com", "test");
        List<TaskDTO> tasks = Arrays.asList(task1, task2);

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
        TaskDTO task = new TaskDTO("Task 1", "Description 1", "TODO", "HIGH", "sam@gmail.com", "sam");

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
    void shouldCreateTask() throws Exception {
        TaskDTO inputTask = new TaskDTO("New Task", "Description", "TODO", "HIGH", "sam@gmail.com", "sam");
        TaskDTO createdTask = new TaskDTO("New Task", "Description", "TODO", "HIGH", "sam@gmail.com", "sam");

        Mockito.when(taskService.createTask(any(TaskDTO.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.ownerEmail").value("sam@gmail.com"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingTaskWithInvalidOwner() throws Exception {
        TaskDTO inputTask = new TaskDTO("New Task", "Description", "TODO", "HIGH", "invalid@gmail.com", "invalid");
        String errorMessage = "User with email: invalid@gmail.com not found";

        Mockito.when(taskService.createTask(any(TaskDTO.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTask)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Long taskId = 1L;
        TaskDTO updateInfo = new TaskDTO("Updated Task", "Updated Description", "DONE", "LOW", "sam@gmail.com", "sam");
        TaskDTO updatedTask = new TaskDTO("Updated Task", "Updated Description", "DONE", "LOW", "sam@gmail.com", "sam");

        Mockito.when(taskService.updateTask(eq(taskId), any(TaskDTO.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Long taskId = 1L;

        Mockito.doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }
}
