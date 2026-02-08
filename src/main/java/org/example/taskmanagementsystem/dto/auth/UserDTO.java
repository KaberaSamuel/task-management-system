package org.example.taskmanagementsystem.dto.auth;

import org.example.taskmanagementsystem.model.User;

public class UserDTO {
    private String username;
    private String email;
    private String role;

    public UserDTO() {}

    public UserDTO(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
