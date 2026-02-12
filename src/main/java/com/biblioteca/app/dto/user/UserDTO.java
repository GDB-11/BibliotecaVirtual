package com.biblioteca.app.dto.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para transferencia de datos de usuarios.
 * Versión simplificada de la entidad User sin información sensible.
 */
public class UserDTO {
    
    private String userId;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
    
    private String statusId;
    private String statusName;
    
    private List<String> roleNames = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructores
    public UserDTO() {
    }

    public UserDTO(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public UserDTO(String userId, String email, String statusName, List<String> roleNames) {
        this.userId = userId;
        this.email = email;
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

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        return roleNames != null && roleNames.stream()
            .anyMatch(role -> role.equalsIgnoreCase(roleName));
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdmin() {
        return hasRole("Admin");
    }

    /**
     * Verifica si el usuario está activo
     */
    public boolean isActive() {
        return "Active".equalsIgnoreCase(statusName);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", statusName='" + statusName + '\'' +
                ", roles=" + roleNames +
                '}';
    }
}