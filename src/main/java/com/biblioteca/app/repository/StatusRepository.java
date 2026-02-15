package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.Status;

/**
 * Repositorio JPA para la entidad Status.
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {

    /**
     * Busca un estado por su nombre
     * 
     * @param statusName Nombre del estado (Active, Inactive)
     * @return Optional con el estado si existe
     */
    Optional<Status> findByStatusName(String statusName);

    /**
     * Verifica si existe un estado con el nombre dado
     * 
     * @param statusName Nombre del estado
     * @return true si existe, false si no
     */
    boolean existsByStatusName(String statusName);
<<<<<<< HEAD
    

=======
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
}