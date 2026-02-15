package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.BookStatus;

@Repository
public interface BookStatusRepository extends JpaRepository<BookStatus, UUID> {
    
    /**
     * Busca un estado por su nombre
     */
    Optional<BookStatus> findByBookStatusName(String bookStatusName);
    
    /**
     * Verifica si existe un estado con el nombre dado
     */
    boolean existsByBookStatusName(String bookStatusName);
}