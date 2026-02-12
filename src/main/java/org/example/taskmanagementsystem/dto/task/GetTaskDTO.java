package org.example.taskmanagementsystem.dto.task;

import org.example.taskmanagementsystem.enums.TaskPriority;
import org.example.taskmanagementsystem.enums.TaskStatus;
import org.example.taskmanagementsystem.model.Task;

public class GetTaskDTO extends CreateTaskDTO {
    private Long id;
    private String ownerEmail;

    public GetTaskDTO() {}

    public GetTaskDTO(Long id, String title, String description, TaskStatus status, TaskPriority priority, String ownerEmail) {
        super(title, description, status, priority);
        this.id = id;

        // add owner Email
        this.ownerEmail = ownerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail =ownerEmail;
    }

    public static GetTaskDTO fromTask(Task task) {
        return new GetTaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getOwner() != null ? task.getOwner().getEmail() : null
        );
    }
}