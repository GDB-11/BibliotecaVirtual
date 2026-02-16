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

import com.biblioteca.app.dto.BookActiveDTO;
import com.biblioteca.app.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

    /**
     * Busca un libro por ISBN
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Busca libros por título (búsqueda parcial)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Busca libros por autor
     */
    List<Book> findByAuthor_AuthorId(UUID authorId);

    /**
     * Busca libros por categoría
     */
    List<Book> findByCategory_CategoryId(UUID categoryId);

    /**
     * Verifica si existe un libro con el ISBN dado
     */
    boolean existsByIsbn(String isbn);

    /**
     * Cuenta el total de libros activos
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.bookStatus.bookStatusName = 'Activo'")
    long countActiveBooks();

    /**
     * Cuenta el total de libros
     */
    long count();

    /**
     * Búsqueda con filtros opcionales y paginación
     * Busca por título o ISBN, autor, categoría y estado del libro
     */
    @Query("""
        SELECT b FROM Book b
        WHERE (:search IS NULL OR :search = '' 
            OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:authorId IS NULL OR b.author.authorId = :authorId)
        AND (:categoryId IS NULL OR b.category.categoryId = :categoryId)
        AND (:bookStatusId IS NULL OR b.bookStatus.bookStatusId = :bookStatusId)
        ORDER BY b.title ASC
    """)
    Page<Book> findAllWithFilters(
            @Param("search") String search,
            @Param("authorId") UUID authorId,
            @Param("categoryId") UUID categoryId,
            @Param("bookStatusId") UUID bookStatusId,
            Pageable pageable
    );

    /**
     * Cuenta libros con filtros
     */
    @Query("""
        SELECT COUNT(b) FROM Book b
        WHERE (:search IS NULL OR :search = '' 
            OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:authorId IS NULL OR b.author.authorId = :authorId)
        AND (:categoryId IS NULL OR b.category.categoryId = :categoryId)
        AND (:bookStatusId IS NULL OR b.bookStatus.bookStatusId = :bookStatusId)
    """)
    long countWithFilters(
            @Param("search") String search,
            @Param("authorId") UUID authorId,
            @Param("categoryId") UUID categoryId,
            @Param("bookStatusId") UUID bookStatusId
    );

    /**
     * Cuenta libros que tienen alquileres activos
     */
    @Query("""
        SELECT COUNT(DISTINCT b) FROM Book b
        JOIN BookCopy bc ON b.bookId = bc.book.bookId
        JOIN Rental r ON bc.bookCopyId = r.bookCopy.bookCopyId
        WHERE r.rentalStatus.rentalStatusName = 'En Proceso'
    """)
    long countBooksWithActiveRentals();

    @Query(value = """
        SELECT 
            b.Title AS titulo,
            b.ISBN AS isbn,
            a.FullName AS autor,
            c.CategoryName AS categoria,
            b.PublicationYear AS anioPublicacion
        FROM Book b
        INNER JOIN Author a ON b.AuthorId = a.AuthorId
        INNER JOIN Category c ON b.CategoryId = c.CategoryId
        INNER JOIN BookStatus bs ON b.BookStatusId = bs.BookStatusId
        WHERE bs.BookStatusName = 'Activo'
        ORDER BY b.Title
    """, nativeQuery = true)
    List<BookActiveDTO> findActiveBooks();

    //USUARIO

    /**
     * Busca libros por categoría
     */
    Page<Book> findByCategory_CategoryId(UUID categoryId, Pageable pageable);

    /**
     * Busca libros por título (búsqueda parcial)
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}