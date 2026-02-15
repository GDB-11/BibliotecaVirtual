package com.biblioteca.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.repository.RentalRepository;
import com.biblioteca.app.repository.projection.BookRentalStatsProjection;

/**
 * Servicio para la gestión de alquileres
 */
@Service
@Transactional(readOnly = true)
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * Obtiene un alquiler por ID
     */
    public Optional<Rental> findById(UUID rentalId) {
        return rentalRepository.findById(rentalId);
    }

    /**
     * Obtiene alquileres por usuario
     */
    public List<Rental> findByUser(UUID userId) {
        return rentalRepository.findByUser_UserId(userId);
    }

    /**
     * Obtiene alquileres por estado
     */
    public List<Rental> findByStatus(UUID statusId) {
        return rentalRepository.findByRentalStatus_RentalStatusId(statusId);
    }

    /**
     * Obtiene alquileres activos de un usuario
     */
    public List<Rental> findActiveRentalsByUserId(UUID userId) {
        return rentalRepository.findActiveRentalsByUserId(userId);
    }

    /**
     * Obtiene los alquileres más recientes
     */
    public List<Rental> getRecentRentals(int limit) {
        return rentalRepository.findRecentRentals(PageRequest.of(0, limit));
    }

    /**
     * Obtiene alquileres con fecha de vencimiento próxima
     */
    public List<Rental> getUpcomingDueRentals(int daysAhead) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(daysAhead);
        return rentalRepository.findUpcomingDueRentals(now, futureDate);
    }

    /**
     * Obtiene alquileres vencidos
     */
    public List<Rental> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDateTime.now());
    }

    /**
     * Obtiene estadísticas de los libros más pedidos
     */
    public List<BookRentalStatsDTO> getTopRequestedBooks(int limit) {
        List<BookRentalStatsProjection> projections = rentalRepository.getTopRequestedBooks(limit);
        
        return projections.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los alquileres
     */
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    /**
     * Guarda o actualiza un alquiler
     */
    @Transactional
    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }

    /**
     * Elimina un alquiler por ID
     */
    @Transactional
    public void delete(UUID rentalId) {
        rentalRepository.deleteById(rentalId);
    }

    /**
     * Cuenta alquileres activos
     */
    public long countActiveRentals() {
        return rentalRepository.countActiveRentals();
    }

    /**
     * Cuenta el total de alquileres
     */
    public long count() {
        return rentalRepository.count();
    }

    public List<RentalActiveDTO> getActiveRentals() {
        return rentalRepository.findActiveRentals();
    }

    public List<RentalCompleteDTO> getAllRentals() {
        return rentalRepository.findAllRentals();
    }

    // ========== MÉTODOS PRIVADOS DE CONVERSIÓN ==========

    /**
     * Convierte una proyección a DTO
     */
    private BookRentalStatsDTO toDTO(BookRentalStatsProjection projection) {
        return new BookRentalStatsDTO(
            projection.getBookId(),
            projection.getTitle(),
            projection.getIsbn(),
            projection.getAuthorName(),
            projection.getCategoryName(),
            projection.getRentalCount(),
            projection.getActiveRentals()
        );
    }
}