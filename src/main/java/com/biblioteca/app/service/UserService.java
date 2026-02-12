package com.biblioteca.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.dto.user.UserDTO;
import com.biblioteca.app.dto.user.UserData;
import com.biblioteca.app.entity.Role;
import com.biblioteca.app.entity.Status;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.helper.PasswordUtil;
import com.biblioteca.app.repository.RoleRepository;
import com.biblioteca.app.repository.StatusRepository;
import com.biblioteca.app.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios.
 * Maneja autenticación, registro y operaciones CRUD.
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private StatusRepository statusRepository;

    /**
     * Autentica un usuario (para área cliente)
     * 
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado
     * @throws IllegalStateException si la cuenta no está activa
     */
    public User authenticate(String email, String password) throws IllegalStateException {
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return null;
        }
        
        if (!"Active".equalsIgnoreCase(user.getStatus().getStatusName())) {
            throw new IllegalStateException("La cuenta no está activa");
        }
        
        return user;
    }

    /**
     * Autentica un administrador (para área admin)
     * Valida que tenga permisos de Admin.
     * 
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Usuario administrador autenticado
     * @throws IllegalStateException si la cuenta no está activa
     * @throws AccessDeniedException si no tiene permisos de administrador
     */
    public User adminAuthenticate(String email, String password) 
            throws IllegalStateException, AccessDeniedException {
        
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return null;
        }
        
        if (!"Active".equalsIgnoreCase(user.getStatus().getStatusName())) {
            throw new IllegalStateException("La cuenta no está activa");
        }
        
        if (!user.hasRole("Admin")) {
            throw new AccessDeniedException("Acceso denegado: se requieren permisos de administrador");
        }
        
        return user;
    }

    /**
     * Registra un nuevo usuario
     * 
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @param statusId UUID del estado
     * @return Usuario registrado
     */
    @Transactional
    public User register(String email, String password, UUID statusId) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        Status status = statusRepository.findById(statusId)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setStatus(status);
        
        return userRepository.save(user);
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Obtiene un usuario por email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Obtiene todos los usuarios activos
     */
    public List<UserData> getAllActiveUsers() {
        List<User> users = userRepository.findAllActive();
        return users.stream()
            .map(this::mapToUserData)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene usuarios con paginación y filtros
     */
    public PagedResult<UserData> getRegisteredUsers(int page, int pageSize, String search,
                                                     UUID roleId, UUID statusId) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        
        Page<User> springPage = userRepository.findAllWithFilters(search, roleId, statusId, pageable);
        
        List<UserData> userDataList = springPage.getContent().stream()
            .map(this::mapToUserData)
            .collect(Collectors.toList());
        
        return new PagedResult<>(
            userDataList,
            page,
            pageSize,
            (int) springPage.getTotalElements()
        );
    }

    /**
     * Actualiza un usuario
     */
    @Transactional
    public void updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }
        userRepository.save(user);
    }

    /**
     * Cambia la contraseña de un usuario
     */
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userRepository.save(user);
    }

    /**
     * Elimina un usuario
     */
    @Transactional
    public void deleteUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Asigna un rol a un usuario
     */
    @Transactional
    public void assignRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        
        user.addRole(role);
        userRepository.save(user);
    }

    /**
     * Remueve un rol de un usuario
     */
    @Transactional
    public void removeRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        
        user.removeRole(role);
        userRepository.save(user);
    }

    /**
     * Verifica si un usuario tiene un rol
     */
    public boolean hasRole(UUID userId, String roleName) {
        return userRepository.hasRole(userId, roleName);
    }

    /**
     * Cuenta total de usuarios
     */
    public long getTotalUsersCount() {
        return userRepository.count();
    }

    /**
     * Cuenta usuarios activos
     */
    public long getActiveUsersCount() {
        return userRepository.countActive();
    }

    /**
     * Verifica si existe un email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ========== MÉTODOS PRIVADOS DE CONVERSIÓN ==========

    /**
     * Convierte User a UserData
     */
    private UserData mapToUserData(User user) {
        List<String> roleNames = user.getRoles().stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
        
        return new UserData(
            user.getUserId().toString(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getStatus().getStatusId().toString(),
            user.getStatus().getStatusName(),
            roleNames
        );
    }

    /**
     * Convierte User a UserDTO
     */
    public UserDTO mapToDTO(User user) {
        List<String> roleNames = user.getRoles().stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
        
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId().toString());
        dto.setEmail(user.getEmail());
        dto.setStatusId(user.getStatus().getStatusId().toString());
        dto.setStatusName(user.getStatus().getStatusName());
        dto.setRoleNames(roleNames);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
}