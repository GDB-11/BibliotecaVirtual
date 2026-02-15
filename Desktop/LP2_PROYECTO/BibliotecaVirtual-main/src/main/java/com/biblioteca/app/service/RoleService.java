package com.biblioteca.app.service;

import com.biblioteca.app.entity.Role;
import com.biblioteca.app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la gestión de roles.
 * Proporciona operaciones CRUD y consultas relacionadas con roles.
 */
@Service
@Transactional(readOnly = true)
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    /**
     * Obtiene todos los roles del sistema.
     * 
     * @return Lista de todos los roles
     */
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    
    /**
     * Obtiene un rol por su ID.
     * 
     * @param roleId UUID del rol
     * @return Optional con el rol si existe
     */
    public Optional<Role> findById(UUID roleId) {
        return roleRepository.findById(roleId);
    }
    
    /**
     * Obtiene un rol por su nombre.
     * 
     * @param roleName Nombre del rol (Admin, User, etc.)
     * @return Optional con el rol si existe
     */
    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
    
    /**
     * Verifica si existe un rol con el nombre dado.
     * 
     * @param roleName Nombre del rol
     * @return true si existe, false si no
     */
    public boolean existsByName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }
    
    /**
     * Guarda o actualiza un rol.
     * 
     * @param role Rol a guardar
     * @return Rol guardado
     */
    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }
    
    /**
     * Elimina un rol por su ID.
     * 
     * @param roleId UUID del rol a eliminar
     */
    @Transactional
    public void delete(UUID roleId) {
        roleRepository.deleteById(roleId);
    }
    
    /**
     * Obtiene el ID del rol Admin.
     * 
     * @return UUID del rol Admin
     * @throws IllegalStateException si no existe el rol Admin
     */
    public UUID getAdminRoleId() {
        return roleRepository.findByRoleName("Admin")
            .orElseThrow(() -> new IllegalStateException("Rol Admin no encontrado"))
            .getRoleId();
    }
    
    /**
     * Obtiene el ID del rol User.
     * 
     * @return UUID del rol User
     * @throws IllegalStateException si no existe el rol User
     */
    public UUID getUserRoleId() {
        return roleRepository.findByRoleName("User")
            .orElseThrow(() -> new IllegalStateException("Rol User no encontrado"))
            .getRoleId();
    }
    
    /**
     * Cuenta el total de roles.
     * 
     * @return Cantidad de roles
     */
    public long count() {
        return roleRepository.count();
    }
}