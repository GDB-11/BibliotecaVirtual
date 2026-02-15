package com.biblioteca.app.dto.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO simplificado para datos de usuario en listados y tablas.
 * Versión ligera sin relaciones complejas.
 */
public class UserData {
    
    private String userId;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String statusId;
    private String statusName;
    private List<String> roleNames;

    // Constructores
    public UserData() {
        this.roleNames = new ArrayList<>();
    }

    public UserData(String userId, String email, LocalDateTime createdAt, LocalDateTime updatedAt,
                   String statusId, String statusName, List<String> roleNames) {
        this.userId = userId;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.statusId = statusId;
        this.statusName = statusName;
        this.roleNames = roleNames != null ? roleNames : new ArrayList<>();
    }

    // Getters y Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    // Helper methods
    public boolean hasRole(String roleName) {
        return roleNames != null && roleNames.stream()
            .anyMatch(role -> role.equalsIgnoreCase(roleName));
    }

    public boolean isAdmin() {
        return hasRole("Admin");
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(statusName);
    }

    public String getRolesDisplay() {
        return roleNames != null ? String.join(", ", roleNames) : "";
    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", statusName='" + statusName + '\'' +
                '}';
    }
}