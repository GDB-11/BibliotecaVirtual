package com.biblioteca.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.entity.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {

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