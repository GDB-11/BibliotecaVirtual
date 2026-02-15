package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.Role;

/**
 * Repositorio JPA para la entidad Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Busca un rol por su nombre
     * 
     * @param roleName Nombre del rol (Admin, User, etc.)
     * @return Optional con el rol si existe
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Verifica si existe un rol con el nombre dado
     * 
     * @param roleName Nombre del rol
     * @return true si existe, false si no
     */
    boolean existsByRoleName(String roleName);
}