package com.biblioteca.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.biblioteca.app.dto.BookActiveDTO;
import com.biblioteca.app.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

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