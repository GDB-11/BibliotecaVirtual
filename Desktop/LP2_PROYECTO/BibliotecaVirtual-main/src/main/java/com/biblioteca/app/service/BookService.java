package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.BookActiveDTO;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.repository.BookRepository;

/**
 * Servicio para la gestión de libros
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Obtiene un libro por ID
     */
    public Optional<Book> findById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    /**
     * Obtiene un libro por ISBN
     */
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Busca libros por título
     */
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Busca libros por autor
     */
    public List<Book> findByAuthor(UUID authorId) {
        return bookRepository.findByAuthor_AuthorId(authorId);
    }

    /**
     * Busca libros por categoría
     */
    public List<Book> findByCategory(UUID categoryId) {
        return bookRepository.findByCategory_CategoryId(categoryId);
    }

    /**
     * Obtiene todos los libros
     */
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * Guarda o actualiza un libro
     */
    @Transactional
    public Book save(Book book) {
        // Si es un libro nuevo, asegurar que no tenga ID
        if (book.getBookId() == null) {
            return bookRepository.save(book);
        } else {
            // Si tiene ID, usar merge
            return bookRepository.saveAndFlush(book);
        }
    }

    /**
     * Elimina un libro por ID
     */
    @Transactional
    public void delete(UUID bookId) {
        bookRepository.deleteById(bookId);
    }

    /**
     * Verifica si existe un libro con el ISBN dado
     */
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    /**
     * Cuenta el total de libros activos
     */
    public long countActiveBooks() {
        return bookRepository.countActiveBooks();
    }

    /**
     * Cuenta el total de libros
     */
    public long count() {
        return bookRepository.count();
    }

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookActiveDTO> getActiveBooks() {
        return bookRepository.findActiveBooks();
    }
    

    /**
     * Elimina un libro por ID
     */
    @Transactional
    public void deleteById(UUID id) {
        bookRepository.deleteById(id);
    }
    
    
}