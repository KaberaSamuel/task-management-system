package org.example.taskmanagementsystem.dto;

import org.example.taskmanagementsystem.model.Task;

public class TaskDTO {
    private String title;
    private String description;
    private String status;
    private String priority;
    private String ownerEmail;
    private String ownerUsername;

    public TaskDTO() {}

    public TaskDTO(String title, String description, String status, String priority, String ownerEmail, String ownerUsername) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.ownerEmail = ownerEmail;
        this.ownerUsername = ownerUsername;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public static TaskDTO fromTask(Task task) {
        return new TaskDTO(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getOwner() != null ? task.getOwner().getEmail() : null,
                task.getOwner() != null ? task.getOwner().getUsername() : null
        );
    }
}
