package org.example.taskmanagementsystem.dto.task;

import org.example.taskmanagementsystem.model.Task;

public class GetTaskDTO extends CreateTaskDTO {
    private String ownerEmail;

    public GetTaskDTO() {}

    public GetTaskDTO(String title, String description, String status, String priority, String ownerEmail) {
        super(title, description, status, priority);

        // add owner Email
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail =ownerEmail;
    }

    public static GetTaskDTO fromTask(Task task) {
        return new GetTaskDTO(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getOwner() != null ? task.getOwner().getEmail() : null
        );
    }
}