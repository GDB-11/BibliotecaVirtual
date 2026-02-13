package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.repository.BookCopyRepository;

/**
 * Servicio para la gestión de ejemplares de libros
 */
@Service
@Transactional(readOnly = true)
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    /**
     * Obtiene un ejemplar por ID
     */
    public Optional<BookCopy> findById(UUID bookCopyId) {
        return bookCopyRepository.findById(bookCopyId);
    }

    /**
     * Obtiene todos los ejemplares de un libro
     */
    public List<BookCopy> findByBook(UUID bookId) {
        return bookCopyRepository.findByBook_BookId(bookId);
    }

    /**
     * Obtiene ejemplares por estado
     */
    public List<BookCopy> findByStatus(UUID statusId) {
        return bookCopyRepository.findByBookCopyStatus_BookCopyStatusId(statusId);
    }

    /**
     * Obtiene ejemplares disponibles de un libro
     */
    public List<BookCopy> findAvailableCopiesByBookId(UUID bookId) {
        return bookCopyRepository.findAvailableCopiesByBookId(bookId);
    }

    /**
     * Obtiene todos los ejemplares
     */
    public List<BookCopy> findAll() {
        return bookCopyRepository.findAll();
    }

    /**
     * Guarda o actualiza un ejemplar
     */
    @Transactional
    public BookCopy save(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    /**
     * Elimina un ejemplar por ID
     */
    @Transactional
    public void delete(UUID bookCopyId) {
        bookCopyRepository.deleteById(bookCopyId);
    }

    /**
     * Cuenta ejemplares disponibles
     */
    public long countAvailableCopies() {
        return bookCopyRepository.countAvailableCopies();
    }

    /**
     * Cuenta ejemplares alquilados
     */
    public long countRentedCopies() {
        return bookCopyRepository.countRentedCopies();
    }

    /**
     * Cuenta ejemplares disponibles de un libro específico
     */
    public long countAvailableCopiesByBookId(UUID bookId) {
        return bookCopyRepository.countAvailableCopiesByBookId(bookId);
    }

    /**
     * Cuenta el total de ejemplares
     */
    public long count() {
        return bookCopyRepository.count();
    }
}