package com.biblioteca.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.biblioteca.app.dto.BookActiveDTO;
import com.biblioteca.app.entity.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {
    
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
}