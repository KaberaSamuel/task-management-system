package org.example.taskmanagementsystem.enums;

public enum UserRole {
    ADMIN("admin"),
    TEAM_LEAD("team-lead"),
    MEMBER("member");


    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }

    public static UserRole fromString(String role) {
        for (UserRole r : UserRole.values()) {
            if (r.role.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}