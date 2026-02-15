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
 * VERSIÓN FINAL - SIN EntityManager
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
     * Guarda un nuevo ejemplar
     */
    @Transactional
    public BookCopy save(BookCopy bookCopy) {
        if (bookCopy.getBookCopyId() == null) {
            bookCopy.setBookCopyId(UUID.randomUUID());
        }
        return bookCopyRepository.save(bookCopy);
    }

    /**
     * Actualiza un ejemplar existente - usa saveAndFlush
     */
    @Transactional
    public BookCopy update(BookCopy bookCopy) {
        if (bookCopy.getBookCopyId() == null) {
            throw new IllegalArgumentException("El ID del ejemplar es requerido para actualizar");
        }
        return bookCopyRepository.saveAndFlush(bookCopy);
    }

    /**
     * Guarda o actualiza según corresponda
     */
    @Transactional
    public BookCopy saveOrUpdate(BookCopy bookCopy) {
        if (bookCopy.getBookCopyId() == null) {
            bookCopy.setBookCopyId(UUID.randomUUID());
            return bookCopyRepository.save(bookCopy);
        } else {
            return bookCopyRepository.saveAndFlush(bookCopy);
        }
    }

    /**
     * Elimina un ejemplar por ID
     */
    @Transactional
    public void deleteById(UUID id) {
        if (hasRentals(id)) {
            throw new IllegalStateException("No se puede eliminar el ejemplar porque tiene alquileres asociados");
        }
        bookCopyRepository.deleteById(id);
    }

    /**
     * Verifica si un ejemplar tiene alquileres asociados
     */
    public boolean hasRentals(UUID bookCopyId) {
        return bookCopyRepository.countRentalsByBookCopyId(bookCopyId) > 0;
    }

    /**
     * Cuenta alquileres de un ejemplar
     */
    public long countRentals(UUID bookCopyId) {
        return bookCopyRepository.countRentalsByBookCopyId(bookCopyId);
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