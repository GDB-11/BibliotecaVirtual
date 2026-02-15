package com.biblioteca.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.repository.RentalRepository;
import com.biblioteca.app.repository.projection.BookRentalStatsProjection;
import com.biblioteca.app.helper.PageMapper;

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

    /**
     * Busca alquileres activos con filtros y paginación
     * 
     * @param page Número de página (1-indexed)
     * @param size Tamaño de página
     * @param search Término de búsqueda
     * @param statusFilter Filtro de estado (vencer/vencido)
     * @param dueSoonDays Días para considerar "por vencer"
     * @param dateFrom Fecha desde (formato yyyy-MM-dd)
     * @param dateTo Fecha hasta (formato yyyy-MM-dd)
     * @return Resultado paginado de alquileres activos
     */
    public PagedResult<Rental> findActiveRentals(
            int page, 
            int size, 
            String search, 
            String statusFilter, 
            int dueSoonDays,
            String dateFrom, 
            String dateTo) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        
        LocalDateTime dateFromParsed = null;
        LocalDateTime dateToParsed = null;
        
        if (dateFrom != null && !dateFrom.isEmpty()) {
            dateFromParsed = LocalDateTime.parse(dateFrom + "T00:00:00");
        }
        
        if (dateTo != null && !dateTo.isEmpty()) {
            dateToParsed = LocalDateTime.parse(dateTo + "T23:59:59");
        }
        
        Page<Rental> rentalsPage = rentalRepository.findActiveRentalsWithFilters(
            search, dateFromParsed, dateToParsed, pageable);
        
        List<Rental> filteredRentals = rentalsPage.getContent();
        
        // Filtrar por estado si es necesario
        if (statusFilter != null && !statusFilter.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dueSoonThreshold = now.plusDays(dueSoonDays);
            
            if ("vencer".equals(statusFilter)) {
                filteredRentals = filteredRentals.stream()
                    .filter(r -> r.getDueDate().isAfter(now) && r.getDueDate().isBefore(dueSoonThreshold))
                    .collect(Collectors.toList());
            } else if ("vencido".equals(statusFilter)) {
                filteredRentals = filteredRentals.stream()
                    .filter(r -> r.getDueDate().isBefore(now))
                    .collect(Collectors.toList());
            }
        }
        
        return PageMapper.toPagedResult(filteredRentals, rentalsPage.getTotalElements(), page, size);
    }

    /**
     * Cuenta alquileres activos que estan al dia
     */
    public int getOnTimeRentalsCount(int dueSoonDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonThreshold = now.plusDays(dueSoonDays);
        return (int) rentalRepository.countOnTimeRentals(dueSoonThreshold);
    }

    /**
     * Cuenta alquileres que estan por vencer pronto
     */
    public int getDueSoonRentalsCount(int dueSoonDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonThreshold = now.plusDays(dueSoonDays);
        return (int) rentalRepository.countDueSoonRentals(now, dueSoonThreshold);
    }

    /**
     * Cuenta alquileres vencidos
     */
    public int getOverdueRentalsCount() {
        LocalDateTime now = LocalDateTime.now();
        return (int) rentalRepository.countOverdueRentals(now);
    }

    /**
     * Obtiene el total de alquileres activos
     */
    public int getActiveRentalsCount() {
        return (int) rentalRepository.countActiveRentals();
    }

    // ========== METODOS PRIVADOS DE CONVERSIÓN ==========

    /**
     * Convierte una proyeccion a DTO
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