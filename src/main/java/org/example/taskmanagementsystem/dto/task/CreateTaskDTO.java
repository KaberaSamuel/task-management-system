package org.example.taskmanagementsystem.dto.task;

import org.example.taskmanagementsystem.enums.TaskPriority;
import org.example.taskmanagementsystem.enums.TaskStatus;

public class CreateTaskDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;

    public CreateTaskDTO() {}

    public CreateTaskDTO(String title, String description, TaskStatus status, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
}
