package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.BookCopyStatus;

@Repository
public interface BookCopyStatusRepository extends JpaRepository<BookCopyStatus, UUID> {

    /**
<<<<<<< HEAD
     * Busca un estado por nombre
     */
    Optional<BookCopyStatus> findByBookCopyStatusName(String name);
=======
     * Busca un estado de ejemplar por su nombre
     */
    Optional<BookCopyStatus> findByBookCopyStatusName(String statusName);
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
}