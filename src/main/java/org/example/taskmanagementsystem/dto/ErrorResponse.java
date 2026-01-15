package org.example.taskmanagementsystem.dto;

public record ErrorResponse(
        int status,
        String error
) {}