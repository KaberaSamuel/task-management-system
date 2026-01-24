package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get task by id
    public Optional<Task> getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
        return Optional.of(task);
    }

    // Create a new task
    public Task createTask(Task task) {
        // use owner from db
        Optional<User> dbOwner = userRepository.findByEmail(task.getOwner().getEmail());
        if (dbOwner.isPresent()) {
            task.setOwner(dbOwner.get());
            return taskRepository.save(task);
        } else {
            throw new ResourceNotFoundException("User with email: " + task.getOwner().getEmail() + " not found");
        }
    }

    // Update an existing task
    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));

        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setStatus(taskDetails.getStatus());
        existingTask.setPriority(taskDetails.getPriority());
        existingTask.setOwner(taskDetails.getOwner());

        return taskRepository.save(existingTask);
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
