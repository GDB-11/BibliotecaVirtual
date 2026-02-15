package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.BookActiveDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.helper.PageMapper;
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
     * Búsqueda paginada de libros con filtros.
     * 
     * @param page Número de página (base 1, primera página = 1)
     * @param size Tamaño de página
     * @param search Texto de búsqueda (título o ISBN)
     * @param authorId Filtro por autor (opcional)
     * @param categoryId Filtro por categoría (opcional)
     * @param bookStatusId Filtro por estado del libro (opcional)
     * @return PagedResult con libros
     */
    public PagedResult<Book> findAllPaginated(int page, int size, String search, 
                                               UUID authorId, UUID categoryId, UUID bookStatusId) {
        int springPage = PageMapper.toSpringPageNumber(page);
        
        Pageable pageable = PageRequest.of(springPage, size, Sort.by("title").ascending());
        
        Page<Book> springPageResult = bookRepository.findAllWithFilters(
            search, authorId, categoryId, bookStatusId, pageable
        );
        
        return PageMapper.toPagedResult(springPageResult, page);
    }

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
        return bookRepository.save(book);
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

    /**
     * Cuenta libros con filtros
     */
    public long countWithFilters(String search, UUID authorId, UUID categoryId, UUID bookStatusId) {
        return bookRepository.countWithFilters(search, authorId, categoryId, bookStatusId);
    }

    /**
     * Cuenta libros que tienen alquileres activos
     */
    public long countBooksWithActiveRentals() {
        return bookRepository.countBooksWithActiveRentals();
    }

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookActiveDTO> getActiveBooks() {
        return bookRepository.findActiveBooks();
    }
}