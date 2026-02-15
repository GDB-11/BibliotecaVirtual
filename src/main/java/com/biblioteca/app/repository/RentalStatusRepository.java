package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.app.entity.RentalStatus;

public interface RentalStatusRepository extends JpaRepository<RentalStatus, UUID> {
    
    /**
     * Busca estado de alquiler por nombre
     */
    Optional<RentalStatus> findByRentalStatusName(String name);
}