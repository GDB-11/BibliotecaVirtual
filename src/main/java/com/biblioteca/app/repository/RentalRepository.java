package com.biblioteca.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.repository.projection.BookRentalStatsProjection;

public interface RentalRepository extends JpaRepository<Rental, UUID> {

    /**
     * Busca alquileres por usuario
     */
    List<Rental> findByUser_UserId(UUID userId);

    /**
     * Busca alquileres por estado
     */
    List<Rental> findByRentalStatus_RentalStatusId(UUID statusId);

    /**
     * Busca alquileres activos (en proceso) de un usuario
     */
    @Query("SELECT r FROM Rental r WHERE r.user.userId = :userId AND r.rentalStatus.rentalStatusName = 'En Proceso'")
    List<Rental> findActiveRentalsByUserId(@Param("userId") UUID userId);

    /**
     * Obtiene los alquileres más recientes
     */
    @Query("SELECT r FROM Rental r ORDER BY r.rentalDate DESC")
    List<Rental> findRecentRentals(Pageable pageable);

    /**
     * Obtiene alquileres con fecha de vencimiento próxima
     */
    @Query("SELECT r FROM Rental r WHERE r.rentalStatus.rentalStatusName = 'En Proceso' AND r.dueDate BETWEEN :startDate AND :endDate ORDER BY r.dueDate ASC")
    List<Rental> findUpcomingDueRentals(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Obtiene alquileres vencidos
     */
    @Query("SELECT r FROM Rental r WHERE r.rentalStatus.rentalStatusName = 'En Proceso' AND r.dueDate < :currentDate ORDER BY r.dueDate ASC")
    List<Rental> findOverdueRentals(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Obtiene estadísticas de los libros más pedidos
     */
    @Query(value = """
        SELECT 
            BIN_TO_UUID(b.BookId) as bookId,
            b.Title as title,
            b.ISBN as isbn,
            a.FullName as authorName,
            c.CategoryName as categoryName,
            COUNT(r.RentalId) as rentalCount,
            SUM(CASE WHEN rs.RentalStatusName = 'En Proceso' THEN 1 ELSE 0 END) as activeRentals
        FROM Book b
        INNER JOIN Author a ON b.AuthorId = a.AuthorId
        INNER JOIN Category c ON b.CategoryId = c.CategoryId
        INNER JOIN BookCopy bc ON b.BookId = bc.BookId
        INNER JOIN Rental r ON bc.BookCopyId = r.BookCopyId
        INNER JOIN RentalStatus rs ON r.RentalStatusId = rs.RentalStatusId
        GROUP BY b.BookId, b.Title, b.ISBN, a.FullName, c.CategoryName
        ORDER BY rentalCount DESC, b.Title ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<BookRentalStatsProjection> getTopRequestedBooks(@Param("limit") int limit);

    /**
     * Cuenta alquileres activos
     */
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.rentalStatus.rentalStatusName = 'En Proceso'")
    long countActiveRentals();

    /**
     * Cuenta total de alquileres
     */
    long count();
    
    @Query(value = """
        SELECT 
            r.RentalId AS rentalId,
            u.Email AS usuario,
            b.Title AS libro,
            r.RentalDate AS fechaAlquiler,
            r.DueDate AS fechaVencimiento,
            DATEDIFF(r.DueDate, CURDATE()) AS diasParaVencer,
            rs.RentalStatusName AS estadoAlquiler
        FROM Rental r
        INNER JOIN `User` u ON r.UserId = u.UserId
        INNER JOIN BookCopy bc ON r.BookCopyId = bc.BookCopyId
        INNER JOIN Book b ON bc.BookId = b.BookId
        INNER JOIN RentalStatus rs ON r.RentalStatusId = rs.RentalStatusId
        WHERE rs.RentalStatusName = 'En Proceso'
          AND r.ReturnDate IS NULL
        ORDER BY r.DueDate ASC
    """, nativeQuery = true)
    List<RentalActiveDTO> findActiveRentals();

    @Query(value = """
        SELECT 
            r.RentalId AS rentalId,
            u.Email AS usuario,
            b.Title AS libro,
            r.RentalDate AS fechaAlquiler,
            r.DueDate AS fechaVencimiento,
            r.ReturnDate AS fechaDevolucion,
            r.TotalCost AS costoTotal,
            rs.RentalStatusName AS estadoAlquiler
        FROM Rental r
        INNER JOIN `User` u ON r.UserId = u.UserId
        INNER JOIN BookCopy bc ON r.BookCopyId = bc.BookCopyId
        INNER JOIN Book b ON bc.BookId = b.BookId
        INNER JOIN RentalStatus rs ON r.RentalStatusId = rs.RentalStatusId
        ORDER BY r.RentalDate
    """, nativeQuery = true)
    List<RentalCompleteDTO> findAllRentals();
}