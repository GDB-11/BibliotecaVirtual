package com.biblioteca.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.repository.projection.BookMostRequestedProjection;
import com.biblioteca.app.repository.projection.BookRentalStatsProjection;

public interface RentalRepository extends JpaRepository<Rental, UUID> {

    // ====== BÚSQUEDAS BÁSICAS ======

    List<Rental> findByUser_UserId(UUID userId);

    Page<Rental> findByUser_UserId(UUID userId, Pageable pageable);

    List<Rental> findByRentalStatus_RentalStatusId(UUID statusId);

    @Query("SELECT r FROM Rental r WHERE r.user.userId = :userId AND r.rentalStatus.rentalStatusName = 'En Proceso'")
    List<Rental> findActiveRentalsByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Rental r ORDER BY r.rentalDate DESC")
    List<Rental> findRecentRentals(Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.rentalStatus.rentalStatusName = 'En Proceso' AND r.dueDate BETWEEN :startDate AND :endDate ORDER BY r.dueDate ASC")
    List<Rental> findUpcomingDueRentals(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Rental r WHERE r.rentalStatus.rentalStatusName = 'En Proceso' AND r.dueDate < :currentDate ORDER BY r.dueDate ASC")
    List<Rental> findOverdueRentals(@Param("currentDate") LocalDateTime currentDate);

    // ====== TOP REQUESTED (STATS) ======

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

    // ====== COUNTS GENERALES ======

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.rentalStatus.rentalStatusName = 'En Proceso'")
    long countActiveRentals();

    // (count() ya viene de JpaRepository, pero tenerlo no rompe)
    long count();

    // ====== LISTADOS PARA DTOs ======

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

    // ====== FILTROS ACTIVOS CON PAGINACIÓN ======

    @Query(value = """
        SELECT r FROM Rental r
        WHERE r.rentalStatus.rentalStatusName = 'En Proceso'
        AND r.returnDate IS NULL
        AND (:search IS NULL OR :search = '' OR 
             LOWER(r.bookCopy.book.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(r.bookCopy.book.author.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(r.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
             CAST(r.rentalId AS string) LIKE CONCAT('%', :search, '%'))
        AND (:dateFrom IS NULL OR r.rentalDate >= :dateFrom)
        AND (:dateTo IS NULL OR r.rentalDate <= :dateTo)
        ORDER BY r.dueDate ASC
    """)
    Page<Rental> findActiveRentalsWithFilters(
            @Param("search") String search,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );

    // ====== COUNTS PARA DASHBOARD (ACTIVOS) ======

    @Query(value = """
        SELECT COUNT(r) FROM Rental r
        WHERE r.rentalStatus.rentalStatusName = 'En Proceso'
        AND r.returnDate IS NULL
        AND r.dueDate > :dueSoonThreshold
    """)
    long countOnTimeRentals(@Param("dueSoonThreshold") LocalDateTime dueSoonThreshold);

    @Query(value = """
        SELECT COUNT(r) FROM Rental r
        WHERE r.rentalStatus.rentalStatusName = 'En Proceso'
        AND r.returnDate IS NULL
        AND r.dueDate > :now
        AND r.dueDate <= :dueSoonThreshold
    """)
    long countDueSoonRentals(
            @Param("now") LocalDateTime now,
            @Param("dueSoonThreshold") LocalDateTime dueSoonThreshold
    );

    @Query(value = """
        SELECT COUNT(r) FROM Rental r
        WHERE r.rentalStatus.rentalStatusName = 'En Proceso'
        AND r.returnDate IS NULL
        AND r.dueDate < :now
    """)
    long countOverdueRentals(@Param("now") LocalDateTime now);

    // ====== MÉTRICAS USUARIO ======

    long countByUser_UserId(UUID userId);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.user.userId = :userId AND r.rentalStatus.rentalStatusName = 'En Proceso'")
    long countActiveByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.user.userId = :userId AND r.rentalStatus.rentalStatusName = 'En Proceso' AND r.dueDate < CURRENT_TIMESTAMP")
    long countOverdueByUser(@Param("userId") UUID userId);

    // ====== MOST REQUESTED (PAGINADO) ======

    @Query(value = """
        SELECT COUNT(DISTINCT b.BookId) 
        FROM Book b
        INNER JOIN Author a ON b.AuthorId = a.AuthorId
        INNER JOIN Category c ON b.CategoryId = c.CategoryId
        INNER JOIN BookCopy bc ON b.BookId = bc.BookId
        INNER JOIN Rental r ON bc.BookCopyId = r.BookCopyId
        WHERE (:categoryId IS NULL OR b.CategoryId = UUID_TO_BIN(:categoryId))
    """, nativeQuery = true)
    long countMostRequestedBooks(@Param("categoryId") String categoryId);

    @Query(value = """
        SELECT 
            BIN_TO_UUID(b.BookId) as bookId,
            b.Title as title,
            b.ISBN as isbn,
            a.FullName as authorName,
            c.CategoryName as categoryName,
            COUNT(r.RentalId) as totalRentals,
            SUM(CASE 
                WHEN DATE(r.RentalDate) = DATE(DATE_SUB(NOW(), INTERVAL 1 DAY)) 
                THEN 1 ELSE 0 
            END) as yesterdayRentals,
            SUM(CASE 
                WHEN DATE(r.RentalDate) = CURDATE() 
                THEN 1 ELSE 0 
            END) as todayRentals
        FROM Book b
        INNER JOIN Author a ON b.AuthorId = a.AuthorId
        INNER JOIN Category c ON b.CategoryId = c.CategoryId
        INNER JOIN BookCopy bc ON b.BookId = bc.BookId
        INNER JOIN Rental r ON bc.BookCopyId = r.BookCopyId
        WHERE (:categoryId IS NULL OR b.CategoryId = UUID_TO_BIN(:categoryId))
        GROUP BY b.BookId, b.Title, b.ISBN, a.FullName, c.CategoryName
        ORDER BY totalRentals DESC, b.Title ASC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<BookMostRequestedProjection> findMostRequestedBooks(
            @Param("categoryId") String categoryId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * Busca alquileres con filtros para el panel de administración
     */
    @Query(value = """
        SELECT r FROM Rental r
        LEFT JOIN r.user u
        LEFT JOIN r.bookCopy bc
        LEFT JOIN bc.book b
        LEFT JOIN b.author a
        LEFT JOIN r.rentalStatus rs
        WHERE (:search IS NULL OR :search = '' OR 
               LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(a.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:userId IS NULL OR u.userId = :userId)
        AND (:rentalStatusId IS NULL OR rs.rentalStatusId = :rentalStatusId)
        ORDER BY r.rentalDate DESC
    """)
    Page<Rental> findAllWithFilters(
            @Param("search") String search,
            @Param("userId") UUID userId,
            @Param("rentalStatusId") UUID rentalStatusId,
            Pageable pageable
    );
}
