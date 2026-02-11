package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.dto.task.CreateTaskDTO;
import org.example.taskmanagementsystem.dto.task.GetTaskDTO;
import org.example.taskmanagementsystem.enums.UserRole;
import org.example.taskmanagementsystem.exception.AccessDeniedException;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
    }

    private static User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private static boolean isActionPermitted(Task task) {
        User currentUser = getAuthenticatedUser();

        // Check permissions (Only Admin and Task Owners are permitted)
        if (currentUser == null) {
            throw new AccessDeniedException("User not logged in");
        } else if ( currentUser.getRole() == UserRole.ADMIN) {
            return true;
        } else return currentUser.getEmail().equals(task.getOwner().getEmail());
    }

    // Get all tasks
    public List<GetTaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(GetTaskDTO::fromTask)
                .collect(Collectors.toList());
    }

    // Get task by id
    public Optional<GetTaskDTO> getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
        return Optional.of(GetTaskDTO.fromTask(task));
    }

    // Create a new task
    public GetTaskDTO createTask(CreateTaskDTO taskDto) {
        User currentUser = getAuthenticatedUser();
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setOwner(currentUser);

        Task savedTask = taskRepository.save(task);
        return GetTaskDTO.fromTask(savedTask);
    }

    // Update an existing task
    public GetTaskDTO updateTask(Long id, CreateTaskDTO taskDetailsDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));

        boolean isPermitted = isActionPermitted(existingTask);
        if (!isPermitted) {
            throw new AccessDeniedException("Action not  permitted");
        }

        existingTask.setTitle(taskDetailsDto.getTitle());
        existingTask.setDescription(taskDetailsDto.getDescription());
        existingTask.setStatus(taskDetailsDto.getStatus());
        existingTask.setPriority(taskDetailsDto.getPriority());

        Task updatedTask = taskRepository.save(existingTask);
        return GetTaskDTO.fromTask(updatedTask);
    }

    // Delete a task by id
    public void deleteTask(Long id) {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
            if (isActionPermitted(task)) {
                taskRepository.delete(task);
            } else {
                throw new AccessDeniedException("Action not permitted");
            }
    }
}
