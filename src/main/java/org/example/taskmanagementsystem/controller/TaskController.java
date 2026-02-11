package org.example.taskmanagementsystem.controller;

import org.example.taskmanagementsystem.dto.task.CreateTaskDTO;
import org.example.taskmanagementsystem.dto.task.GetTaskDTO;
import org.example.taskmanagementsystem.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Get all tasks
    @GetMapping
    public ResponseEntity<List<GetTaskDTO>> getAllTasks() {
        List<GetTaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Get task by id
    @GetMapping("/{id}")
    public ResponseEntity<GetTaskDTO> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new task
    @PostMapping
    public ResponseEntity<GetTaskDTO> createTask(@RequestBody CreateTaskDTO taskDto) {
        GetTaskDTO createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    // Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<GetTaskDTO> updateTask(@PathVariable Long id, @RequestBody CreateTaskDTO taskDetailsDto) {
        GetTaskDTO updatedTask = taskService.updateTask(id, taskDetailsDto);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete a task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
