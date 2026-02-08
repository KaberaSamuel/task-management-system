package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.dto.task.CreateTaskDTO;
import org.example.taskmanagementsystem.dto.task.GetTaskDTO;
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
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
        // get owner from security context
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User dbOwner = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + currentUserEmail + " not found"));

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setOwner(dbOwner);

        Task savedTask = taskRepository.save(task);
        return GetTaskDTO.fromTask(savedTask);
    }

    // Update an existing task
    public GetTaskDTO updateTask(Long id, CreateTaskDTO taskDetailsDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));

        existingTask.setTitle(taskDetailsDto.getTitle());
        existingTask.setDescription(taskDetailsDto.getDescription());
        existingTask.setStatus(taskDetailsDto.getStatus());
        existingTask.setPriority(taskDetailsDto.getPriority());

        Task updatedTask = taskRepository.save(existingTask);
        return GetTaskDTO.fromTask(updatedTask);
    }

    // Delete a task by id
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Task with id: " + id + " not found");
        }
    }
}
