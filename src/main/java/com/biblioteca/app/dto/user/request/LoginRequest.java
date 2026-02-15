package com.biblioteca.app.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de login.
 * Contiene las credenciales del usuario.
 */
public class LoginRequest {
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private boolean rememberMe;

    // Constructores
    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginRequest(String email, String password, boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", rememberMe=" + rememberMe +
                '}';
    }
}