package com.biblioteca.app.repository;

import com.biblioteca.app.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad BookStatus.
 * Proporciona métodos CRUD y consultas personalizadas.
 */
@Repository
public interface BookStatusRepository extends JpaRepository<BookStatus, UUID> {
    
    /**
     * Busca un estado de libro por su nombre.
     * 
     * @param bookStatusName Nombre del estado de libro
     * @return Optional con el estado de libro si existe
     */
    Optional<BookStatus> findByBookStatusName(String bookStatusName);
    
    /**
     * Verifica si existe un estado de libro con el nombre dado.
     * 
     * @param bookStatusName Nombre del estado de libro
     * @return true si existe, false si no
     */
    boolean existsByBookStatusName(String bookStatusName);
}