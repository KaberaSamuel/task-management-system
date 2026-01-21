package org.example.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanagementsystem.dto.UserDTO;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests  {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // Test user creation
    @Test
    void shouldCreateNewUser() throws Exception {
        LocalDate today = LocalDate.now();

        // setup expected behavior
        User inputUser = new User(1L,"sam", "sam@gmail.com", "1234",  "USER", today);
        User savedUser = new User(1L,"sam", "sam@gmail.com", "1234",  "USER", today);
        Mockito.when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // Perform the POST request
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1L))
                        .andExpect(jsonPath("$.username").value("sam"))
                        .andExpect(jsonPath("$.email").value("sam@gmail.com"))
                        .andExpect(jsonPath("$.password").value("1234"))
                        .andExpect(jsonPath("$.role").value("USER"))
                        .andExpect(jsonPath("$.createdAt").value(today.toString()));
    }

    // Test get user by id
    @Test
    void ShouldReturnUserById() throws Exception {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        UserDTO mockUserDTO = new UserDTO("sam", "sam@gmail.com", "USER");

        // Define the behavior of the mock service
        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(mockUserDTO));

        // Perform get request
        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.username").value(mockUserDTO.getUsername()))
                        .andExpect(jsonPath("$.email").value(mockUserDTO.getEmail()))
                        .andExpect(jsonPath("$.role").value(mockUserDTO.getRole()));
    }

    // Test user lookup by invalid id
    @Test
    void shouldReturnNotFoundWhenGivenInvalidId() throws Exception {
        Long invalidUserId = 2L;
        String errorMessage = "User with id: " + invalidUserId + " not found";

        // Mock service behavior for a non-existent ID
        Mockito.when(userService.getUserById(invalidUserId)).thenThrow(new ResourceNotFoundException(errorMessage));

        //  Perform get request
        mockMvc.perform(get("/api/users/{id}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }

    // Test user update
    @Test
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        UserDTO updatedUserDTO = new UserDTO("sam", "sam@gmail.com", "USER");

        Mockito.when(userService.updateUser(eq(userId), any(UserDTO.class)))
                .thenReturn(updatedUserDTO);

        // Perform the PUT request
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("sam"))
                .andExpect(jsonPath("$.email").value("sam@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // Test user deletion
    @Test
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;

        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    // Test deletion of non-existing user
    @Test
    void shouldReturnNotFoundWhenDeletingInvalidUser() throws Exception {
        Long invalidUserId = 99L;
        String errorMessage = "User with id: " + invalidUserId + " not found";

        Mockito.doThrow(new ResourceNotFoundException(errorMessage))
                .when(userService).deleteUser(invalidUserId);

        mockMvc.perform(delete("/api/users/{id}", invalidUserId))
                .andExpect(status().isNotFound());
    }
}
