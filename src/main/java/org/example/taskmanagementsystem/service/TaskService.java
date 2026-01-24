package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.dto.TaskDTO;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.example.taskmanagementsystem.repository.UserRepository;
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
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskDTO::fromTask)
                .collect(Collectors.toList());
    }

    // Get task by id
    public Optional<TaskDTO> getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
        return Optional.of(TaskDTO.fromTask(task));
    }

    // Create a new task
    public TaskDTO createTask(TaskDTO taskDto) {
        // use owner from db
        User dbOwner = userRepository.findByEmail(taskDto.getOwnerEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + taskDto.getOwnerEmail() + " not found"));

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setOwner(dbOwner);

        Task savedTask = taskRepository.save(task);
        return TaskDTO.fromTask(savedTask);
    }

    // Update an existing task
    public TaskDTO updateTask(Long id, TaskDTO taskDetailsDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));

        existingTask.setTitle(taskDetailsDto.getTitle());
        existingTask.setDescription(taskDetailsDto.getDescription());
        existingTask.setStatus(taskDetailsDto.getStatus());
        existingTask.setPriority(taskDetailsDto.getPriority());

        if (taskDetailsDto.getOwnerEmail() != null) {
            User dbOwner = userRepository.findByEmail(taskDetailsDto.getOwnerEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User with email: " + taskDetailsDto.getOwnerEmail() + " not found"));
            existingTask.setOwner(dbOwner);
        }

        Task updatedTask = taskRepository.save(existingTask);
        return TaskDTO.fromTask(updatedTask);
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
