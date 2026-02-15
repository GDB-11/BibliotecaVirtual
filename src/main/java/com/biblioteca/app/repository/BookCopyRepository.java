package com.biblioteca.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.BookCopy;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

    /**
     * Encuentra ejemplares por libro
     */
    List<BookCopy> findByBook_BookId(UUID bookId);

    /**
     * Encuentra ejemplares por libro con paginación
     */
    Page<BookCopy> findByBook_BookId(UUID bookId, Pageable pageable);

    /**
     * Encuentra ejemplares por estado
     */
    List<BookCopy> findByBookCopyStatus_BookCopyStatusId(UUID statusId);

    /**
     * Encuentra ejemplares por estado con paginación
     */
    Page<BookCopy> findByBookCopyStatus_BookCopyStatusId(UUID statusId, Pageable pageable);

    /**
     * Encuentra ejemplares por libro y estado
     */
    Page<BookCopy> findByBook_BookIdAndBookCopyStatus_BookCopyStatusId(UUID bookId, UUID statusId, Pageable pageable);

    /**
     * Encuentra ejemplares disponibles de un libro específico
     */
<<<<<<< HEAD
    @Query("SELECT bc FROM BookCopy bc " +
    	       "WHERE bc.book.bookId = :bookId " +
    	       "AND bc.bookCopyStatus.bookCopyStatusName = 'Disponible'")
    	List<BookCopy> findAvailableCopiesByBookId(@Param("bookId") UUID bookId);
=======
    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.bookId = :bookId AND bc.bookCopyStatus.bookCopyStatusName = 'Disponible'")
    List<BookCopy> findAvailableCopiesByBookId(@Param("bookId") UUID bookId);
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d

    /**
     * Búsqueda de ejemplares por título o ISBN del libro
     */
    @Query("SELECT bc FROM BookCopy bc WHERE " +
           "LOWER(bc.book.title) LIKE LOWER(:search) OR " +
           "LOWER(bc.book.isbn) LIKE LOWER(:search)")
    Page<BookCopy> findBySearch(@Param("search") String search, Pageable pageable);

    /**
     * Búsqueda de ejemplares por título o ISBN del libro y estado
     */
    @Query("SELECT bc FROM BookCopy bc WHERE " +
           "(LOWER(bc.book.title) LIKE LOWER(:search) OR LOWER(bc.book.isbn) LIKE LOWER(:search)) " +
           "AND bc.bookCopyStatus.bookCopyStatusId = :statusId")
    Page<BookCopy> findBySearchAndStatus(@Param("search") String search, @Param("statusId") UUID statusId, Pageable pageable);

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
     * Cuenta ejemplares disponibles de un libro específico
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.bookId = :bookId AND bc.bookCopyStatus.bookCopyStatusName = 'Disponible'")
    long countAvailableCopiesByBookId(@Param("bookId") UUID bookId);
}