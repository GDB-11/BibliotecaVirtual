package com.biblioteca.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.BookCopy;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

    /**
     * Busca todos los ejemplares de un libro específico
     */
    List<BookCopy> findByBook_BookId(UUID bookId);

    /**
     * Busca ejemplares por estado
     */
    List<BookCopy> findByBookCopyStatus_BookCopyStatusId(UUID statusId);

    /**
     * Cuenta ejemplares disponibles
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookCopyStatus.bookCopyStatusName = 'Disponible'")
    long countAvailableCopies();

    /**
     * Cuenta ejemplares alquilados
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookCopyStatus.bookCopyStatusName = 'Alquilado'")
    long countRentedCopies();

    /**
     * Cuenta ejemplares de un libro específico que están disponibles
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.bookId = :bookId AND bc.bookCopyStatus.bookCopyStatusName = 'Disponible'")
    long countAvailableCopiesByBookId(@Param("bookId") UUID bookId);

    /**
     * Busca ejemplares disponibles de un libro
     */
    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.bookId = :bookId AND bc.bookCopyStatus.bookCopyStatusName = 'Disponible'")
    List<BookCopy> findAvailableCopiesByBookId(@Param("bookId") UUID bookId);
    
    
    
    /**
     * Cuenta los alquileres asociados a un ejemplar
     */
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.bookCopy.bookCopyId = :bookCopyId")
    long countRentalsByBookCopyId(@Param("bookCopyId") UUID bookCopyId);
    
    
}