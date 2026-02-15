package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.repository.BookCopyStatusRepository;

/**
 * Servicio para la gestión de estados de ejemplares
 */
@Service
@Transactional(readOnly = true)
public class BookCopyStatusService {

    @Autowired
    private BookCopyStatusRepository bookCopyStatusRepository;

    /**
     * Obtiene un estado por ID
     */
    public Optional<BookCopyStatus> findById(UUID statusId) {
        return bookCopyStatusRepository.findById(statusId);
    }

    /**
     * Obtiene todos los estados
     */
    public List<BookCopyStatus> findAll() {
        return bookCopyStatusRepository.findAll();
    }

    /**
     * Busca un estado por nombre
     */
    public Optional<BookCopyStatus> findByName(String statusName) {
        return bookCopyStatusRepository.findByBookCopyStatusName(statusName);
    }

    /**
     * Obtiene el ID del estado "Disponible"
     */
    public UUID getAvailableStatusId() {
        return findByName("Disponible")
                .map(BookCopyStatus::getBookCopyStatusId)
                .orElseThrow(() -> new RuntimeException("Estado 'Disponible' no encontrado"));
    }

    /**
     * Obtiene el ID del estado "Alquilado"
     */
    public UUID getRentedStatusId() {
        return findByName("Alquilado")
                .map(BookCopyStatus::getBookCopyStatusId)
                .orElseThrow(() -> new RuntimeException("Estado 'Alquilado' no encontrado"));
    }

    /**
     * Obtiene el ID del estado "Mantenimiento"
     */
    public UUID getMaintenanceStatusId() {
        return findByName("Mantenimiento")
                .map(BookCopyStatus::getBookCopyStatusId)
                .orElseThrow(() -> new RuntimeException("Estado 'Mantenimiento' no encontrado"));
    }

    /**
     * Obtiene el ID del estado "Descontinuado"
     */
    public UUID getDiscontinuedStatusId() {
        return findByName("Descontinuado")
                .map(BookCopyStatus::getBookCopyStatusId)
                .orElseThrow(() -> new RuntimeException("Estado 'Descontinuado' no encontrado"));
    }
}