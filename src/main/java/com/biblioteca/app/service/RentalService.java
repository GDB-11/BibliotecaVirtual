package com.biblioteca.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.RentalStatus;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.helper.PageMapper;
import com.biblioteca.app.repository.BookCopyRepository;  // ✅ AGREGADO
import com.biblioteca.app.repository.RentalRepository;
import com.biblioteca.app.repository.RentalStatusRepository;
import com.biblioteca.app.repository.projection.BookRentalStatsProjection;
import com.biblioteca.app.repository.UserRepository;

/**
 * Servicio para la gestión de alquileres
 */
@Service
@Transactional(readOnly = true)
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalStatusRepository rentalStatusRepository;
    
    @Autowired
    private BookCopyService bookCopyService;
    
    @Autowired
    private BookCopyStatusService bookCopyStatusService;
    
    @Autowired
    private BookCopyRepository bookCopyRepository;  
    
    @Autowired
    private UserRepository userRepository;
    
    // ========== MÉTODOS EXISTENTES ==========
    
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
    
    // ========== MÉTODOS PARA MÓDULO DE USUARIO ==========

    /**
     * Busca alquileres por usuario con paginación
     */
    public Page<Rental> findByUser(UUID userId, Pageable pageable) {
        return rentalRepository.findByUser_UserId(userId, pageable);
    }

    /**
     * Crea un nuevo alquiler - VERSIÓN CORREGIDA CON BookCopyRepository
     */
    @Transactional
    public Rental createRental(User user, BookCopy bookCopy) {
        System.out.println("=== CREANDO ALQUILER EN RENTALSERVICE ===");
        
        try {
            // 1. Verificar disponibilidad
            if (!"Disponible".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName())) {
                throw new IllegalStateException("El ejemplar no está disponible. Estado actual: " + 
                    bookCopy.getBookCopyStatus().getBookCopyStatusName());
            }
            
            // 2. Buscar estado "Alquilado"
            BookCopyStatus alquiladoStatus = bookCopyStatusService.findByName("Alquilado")
                .orElseThrow(() -> new RuntimeException("Estado 'Alquilado' no encontrado"));
            
            // 3. PRIMERO: Actualizar el estado del ejemplar
            BookCopy freshCopy = bookCopyRepository.findById(bookCopy.getBookCopyId())
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
            
            freshCopy.setBookCopyStatus(alquiladoStatus);
            BookCopy updatedCopy = bookCopyRepository.save(freshCopy);
            bookCopyRepository.flush();
            System.out.println("✓ Ejemplar actualizado a estado: Alquilado");
            
            // 4. Buscar estado "En Proceso" para el alquiler
            RentalStatus status = rentalStatusRepository.findByRentalStatusName("En Proceso")
                .orElseThrow(() -> new RuntimeException("Estado de alquiler 'En Proceso' no encontrado"));
            
            // 5. DESPUÉS: Crear el alquiler con el ejemplar YA ACTUALIZADO
            Rental rental = new Rental();
            rental.setRentalId(UUID.randomUUID());
            rental.setUser(user);
            rental.setBookCopy(updatedCopy); // Usar el ejemplar ya actualizado
            rental.setRentalDate(LocalDateTime.now());
            rental.setDueDate(LocalDateTime.now().plusDays(7));
            rental.setRentalDays(7);
            rental.setDailyRate(BigDecimal.valueOf(5.00));
            rental.setTotalCost(BigDecimal.valueOf(35.00));
            rental.setRentalStatus(status);
            
            // 6. Guardar el alquiler
            Rental savedRental = rentalRepository.save(rental);
            rentalRepository.flush();
            System.out.println("✓ Alquiler guardado con ID: " + savedRental.getRentalId());
            
            return savedRental;
            
        } catch (Exception e) {
            System.out.println("✗ ERROR en createRental: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    
    
    @Transactional
    public Rental createRentalEmergency(UUID userId, UUID bookCopyId) {
        System.out.println("=== MÉTODO DE EMERGENCIA ===");
        
        try {
            // Buscar entidades usando los repositorios
            User user = userRepository.findById(userId)  // ✅ AHORA SÍ FUNCIONA
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
            
            RentalStatus status = rentalStatusRepository.findByRentalStatusName("En Proceso")
                .orElseThrow(() -> new RuntimeException("Estado 'En Proceso' no encontrado"));
            
            BookCopyStatus alquiladoStatus = bookCopyStatusService.findByName("Alquilado")
                .orElseThrow(() -> new RuntimeException("Estado 'Alquilado' no encontrado"));
            
            // PRIMERO: Actualizar el ejemplar
            bookCopy.setBookCopyStatus(alquiladoStatus);
            bookCopyRepository.save(bookCopy);
            bookCopyRepository.flush();
            System.out.println("✓ Ejemplar actualizado a Alquilado");
            
            // DESPUÉS: Crear el alquiler
            Rental rental = new Rental();
            rental.setRentalId(UUID.randomUUID());
            rental.setUser(user);
            rental.setBookCopy(bookCopy);
            rental.setRentalDate(LocalDateTime.now());
            rental.setDueDate(LocalDateTime.now().plusDays(7));
            rental.setRentalDays(7);
            rental.setDailyRate(BigDecimal.valueOf(5.00));
            rental.setTotalCost(BigDecimal.valueOf(35.00));
            rental.setRentalStatus(status);
            
            // Guardar alquiler
            Rental savedRental = rentalRepository.save(rental);
            rentalRepository.flush();
            System.out.println("✓ Alquiler guardado con ID: " + savedRental.getRentalId());
            
            return savedRental;
            
        } catch (Exception e) {
            System.out.println("✗ Error en método de emergencia: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    
    
    
    /**
     * Cuenta alquileres de un usuario
     */
    public long countByUser(UUID userId) {
        return rentalRepository.countByUser_UserId(userId);
    }

    /**
     * Cuenta alquileres activos de un usuario (estado "En Proceso")
     */
    public long countActiveByUser(UUID userId) {
        return rentalRepository.countActiveByUser(userId);
    }

    /**
     * Cuenta alquileres vencidos de un usuario (fecha vencimiento pasada y aún activos)
     */
    public long countOverdueByUser(UUID userId) {
        return rentalRepository.countOverdueByUser(userId);
    }

    /**
     * Obtiene los últimos alquileres de un usuario
     */
    public List<Rental> findLastByUser(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rentalDate"));
        return rentalRepository.findByUser_UserId(userId, pageable).getContent();
    }
}