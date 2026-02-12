package com.biblioteca.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.User;

/**
 * Repositorio JPA para la entidad User.
 * Proporciona métodos CRUD y consultas personalizadas.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por email
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por estado
     * 
     * @param statusId UUID del estado
     * @return Lista de usuarios con ese estado
     */
    List<User> findByStatus_StatusId(UUID statusId);

    /**
     * Busca usuarios activos
     * 
     * @return Lista de usuarios activos
     */
    @Query("SELECT u FROM User u WHERE u.status.statusName = 'Active' ORDER BY u.createdAt DESC")
    List<User> findAllActive();

    /**
     * Búsqueda paginada con filtros
     * 
     * @param search Texto de búsqueda (email)
     * @param roleId Filtro por rol (opcional)
     * @param statusId Filtro por estado (opcional)
     * @param pageable Parámetros de paginación
     * @return Página de usuarios
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN u.userRoles ur
        WHERE (:search IS NULL OR :search = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:roleId IS NULL OR ur.role.roleId = :roleId)
        AND (:statusId IS NULL OR u.status.statusId = :statusId)
        ORDER BY u.createdAt DESC
    """)
    Page<User> findAllWithFilters(
        @Param("search") String search,
        @Param("roleId") UUID roleId,
        @Param("statusId") UUID statusId,
        Pageable pageable
    );

    /**
     * Cuenta usuarios con filtros
     * 
     * @param search Texto de búsqueda
     * @param roleId Filtro por rol
     * @param statusId Filtro por estado
     * @return Cantidad de usuarios que coinciden
     */
    @Query("""
        SELECT COUNT(DISTINCT u) FROM User u
        LEFT JOIN u.userRoles ur
        WHERE (:search IS NULL OR :search = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:roleId IS NULL OR ur.role.roleId = :roleId)
        AND (:statusId IS NULL OR u.status.statusId = :statusId)
    """)
    long countWithFilters(
        @Param("search") String search,
        @Param("roleId") UUID roleId,
        @Param("statusId") UUID statusId
    );

    /**
     * Cuenta usuarios activos
     * 
     * @return Cantidad de usuarios activos
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status.statusName = 'Active'")
    long countActive();

    /**
     * Verifica si un usuario tiene un rol específico
     * 
     * @param userId UUID del usuario
     * @param roleName Nombre del rol
     * @return true si el usuario tiene el rol, false si no
     */
    @Query("""
        SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
        FROM UserRole ur
        WHERE ur.user.userId = :userId
        AND ur.role.roleName = :roleName
    """)
    boolean hasRole(@Param("userId") UUID userId, @Param("roleName") String roleName);
}