package com.biblioteca.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.dto.author.AuthorActiveDTO;
import com.biblioteca.app.entity.Author;
import com.biblioteca.app.repository.projection.AuthorStatsProjection;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID>, JpaSpecificationExecutor<Author> {

    /**
     * Busca un autor por su nombre completo
     */
    Optional<Author> findByFullName(String fullName);

    /**
     * Busca autores por país
     */
    List<Author> findByCountry_CountryIdOrderByFullNameAsc(UUID countryId);

    /**
     * Busca autores por estado
     */
    List<Author> findByStatus_StatusIdOrderByFullNameAsc(UUID statusId);

    /**
     * Verifica si existe un autor con el nombre dado
     */
    boolean existsByFullName(String fullName);

    /**
     * Cuenta el número de libros de un autor
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author.authorId = :authorId")
    int countBooksByAuthorId(@Param("authorId") UUID authorId);

    /**
     * Búsqueda con filtros opcionales y paginación
     * Busca por nombre o pseudónimo, país y estado
     */
    @Query("""
        SELECT a FROM Author a
        WHERE (:search IS NULL OR :search = '' 
            OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(a.pseudonym) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:countryId IS NULL OR a.country.countryId = :countryId)
        AND (:statusId IS NULL OR a.status.statusId = :statusId)
        ORDER BY a.fullName ASC
    """)
    Page<Author> findAllWithFilters(
        @Param("search") String search,
        @Param("countryId") UUID countryId,
        @Param("statusId") UUID statusId,
        Pageable pageable
    );

    /**
     * Cuenta autores con filtros opcionales
     */
    @Query("""
        SELECT COUNT(a) FROM Author a
        WHERE (:search IS NULL OR :search = '' 
            OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(a.pseudonym) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:countryId IS NULL OR a.country.countryId = :countryId)
        AND (:statusId IS NULL OR a.status.statusId = :statusId)
    """)
    long countWithFilters(
        @Param("search") String search,
        @Param("countryId") UUID countryId,
        @Param("statusId") UUID statusId
    );

    /**
     * Obtiene los autores más solicitados con estadísticas completas
     * VERSIÓN COMPLETA (puede dar error por BIN_TO_UUID)
     */
    @Query(value = """
        SELECT 
            BIN_TO_UUID(a.AuthorId) as authorId,
            a.FullName as fullName,
            a.Pseudonym as pseudonym,
            a.PhotoUrl as photoUrl,
            c.CountryName as countryName,
            COUNT(DISTINCT b.BookId) as totalBooks,
            COUNT(DISTINCT bc.BookCopyId) as totalCopies,
            SUM(CASE WHEN bcs.BookCopyStatusName = 'Disponible' THEN 1 ELSE 0 END) as availableCopies,
            COUNT(r.RentalId) as totalRentals
        FROM Author a
        INNER JOIN Country c ON a.CountryId = c.CountryId
        INNER JOIN Status s ON a.StatusId = s.StatusId
        LEFT JOIN Book b ON a.AuthorId = b.AuthorId
        LEFT JOIN BookCopy bc ON b.BookId = bc.BookId
        LEFT JOIN BookCopyStatus bcs ON bc.BookCopyStatusId = bcs.BookCopyStatusId
        LEFT JOIN Rental r ON bc.BookCopyId = r.BookCopyId
        WHERE (:countryId IS NULL OR a.CountryId = UUID_TO_BIN(CAST(:countryId AS CHAR)))
        AND (:statusId IS NULL OR a.StatusId = UUID_TO_BIN(CAST(:statusId AS CHAR)))
        GROUP BY a.AuthorId, a.FullName, a.Pseudonym, a.PhotoUrl, c.CountryName
        HAVING COUNT(r.RentalId) > 0
        ORDER BY totalRentals DESC, a.FullName ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<AuthorStatsProjection> getMostRequestedAuthors(
        @Param("countryId") String countryId,
        @Param("statusId") String statusId,
        @Param("limit") int limit
    );

    /**
     * VERSIÓN SIMPLIFICADA - Autores más solicitados (SIN BIN_TO_UUID)
     * Esta versión funciona en MySQL 8.0 sin funciones especiales
     */
    @Query(value = """
        SELECT 
            a.AuthorId,
            a.FullName,
            c.CountryName,
            COUNT(r.RentalId) as totalRentals
        FROM Author a
        INNER JOIN Country c ON a.CountryId = c.CountryId
        LEFT JOIN Book b ON a.AuthorId = b.AuthorId
        LEFT JOIN BookCopy bc ON b.BookId = bc.BookId
        LEFT JOIN Rental r ON bc.BookCopyId = r.BookCopyId
        GROUP BY a.AuthorId, a.FullName, c.CountryName
        HAVING COUNT(r.RentalId) > 0
        ORDER BY totalRentals DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTopAuthorsSimple(@Param("limit") int limit);

    /**
     * Cuenta autores que tienen al menos un alquiler
     */
    @Query("""
        SELECT COUNT(DISTINCT a) FROM Author a
        JOIN Book b ON a.authorId = b.author.authorId
        JOIN BookCopy bc ON b.bookId = bc.book.bookId
        JOIN Rental r ON bc.bookCopyId = r.bookCopy.bookCopyId
    """)
    long countAuthorsWithRentals();

    /**
     * Obtiene el total de alquileres de todos los autores
     */
    @Query("""
        SELECT COUNT(r) FROM Rental r
        JOIN r.bookCopy bc
        JOIN bc.book b
        JOIN b.author a
    """)
    long getTotalAuthorsRentals();

    /**
     * Cuenta autores que tienen al menos un libro
     */
    @Query("SELECT COUNT(DISTINCT a) FROM Author a JOIN Book b ON a.authorId = b.author.authorId")
    long countAuthorsWithBooks();

    /**
     * Busca todos los autores ordenados por nombre
     */
    List<Author> findAllByOrderByFullNameAsc();

    /**
     * Busca autores activos con información de país
     */
    @Query(value = """
        SELECT 
            a.FullName AS autor,
            c.CountryName AS pais,
            a.BirthYear AS anioNacimiento,
            a.DeathYear AS anioFallecimiento
        FROM Author a
        JOIN Country c ON a.CountryId = c.CountryId
        JOIN Status s ON a.StatusId = s.StatusId
        WHERE s.StatusName = 'Active'
        ORDER BY a.FullName
    """, nativeQuery = true)
    List<AuthorActiveDTO> findActiveAuthors();
}