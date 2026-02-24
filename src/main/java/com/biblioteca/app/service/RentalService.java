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
import com.biblioteca.app.dto.rental.BookMostRequestedDTO;
import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.RentalStatus;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.helper.PageMapper;
import com.biblioteca.app.repository.BookCopyRepository;
import com.biblioteca.app.repository.BookRepository;
import com.biblioteca.app.repository.RentalRepository;
import com.biblioteca.app.repository.RentalStatusRepository;
import com.biblioteca.app.repository.UserRepository;
import com.biblioteca.app.repository.projection.BookMostRequestedProjection;
import com.biblioteca.app.repository.projection.BookRentalStatsProjection;

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

    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private BookRepository bookRepository;

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
     * Obtiene alquileres por usuario con paginación
     */
    public Page<Rental> findByUser(UUID userId, Pageable pageable) {
        return rentalRepository.findByUser_UserId(userId, pageable);
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
     * Obtiene estadísticas de los libros más pedidos (top N)
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

    public int getOnTimeRentalsCount(int dueSoonDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonThreshold = now.plusDays(dueSoonDays);
        return (int) rentalRepository.countOnTimeRentals(dueSoonThreshold);
    }

    public int getDueSoonRentalsCount(int dueSoonDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonThreshold = now.plusDays(dueSoonDays);
        return (int) rentalRepository.countDueSoonRentals(now, dueSoonThreshold);
    }

    public int getOverdueRentalsCount() {
        LocalDateTime now = LocalDateTime.now();
        return (int) rentalRepository.countOverdueRentals(now);
    }

    public int getActiveRentalsCount() {
        return (int) rentalRepository.countActiveRentals();
    }

    /**
     * Obtiene libros más pedidos con paginación y filtro por categoría
     */
    public PagedResult<BookMostRequestedDTO> getMostRequestedBooks(
            int currentPage,
            int pageSize,
            String categoryId) {

        if (currentPage < 1) {
            currentPage = 1;
        }

        int offset = (currentPage - 1) * pageSize;

        long totalItems = rentalRepository.countMostRequestedBooks(categoryId);

        List<BookMostRequestedProjection> projections = rentalRepository.findMostRequestedBooks(
                categoryId, pageSize, offset);

        List<BookMostRequestedDTO> items = projections.stream()
                .map(this::toMostRequestedDTO)
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            Integer maxRentals = items.get(0).getTotalRentals();
            items.forEach(item -> item.setPopularityPercentage(maxRentals));
        }

        return new PagedResult<>(items, currentPage, pageSize, (int) totalItems);
    }

    // ========= CONVERSIÓN DTOs =========

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

    private BookMostRequestedDTO toMostRequestedDTO(BookMostRequestedProjection projection) {
        return new BookMostRequestedDTO(
                projection.getBookId(),
                projection.getTitle(),
                projection.getIsbn(),
                projection.getAuthorName(),
                projection.getCategoryName(),
                projection.getTotalRentals(),
                projection.getYesterdayRentals(),
                projection.getTodayRentals()
        );
    }

    // ========= MÉTODOS PARA MÓDULO DE USUARIO =========

    @Transactional
    public Rental createRental(User user, BookCopy bookCopy) {

        if (!"Disponible".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName())) {
            throw new IllegalStateException("El ejemplar no está disponible");
        }
        
        RentalStatus enProcesoStatus = rentalStatusRepository.findByRentalStatusName("En Proceso")
            .orElseThrow(() -> new RuntimeException("Estado 'En Proceso' no encontrado"));
        
        BookCopyStatus alquiladoStatus = bookCopyStatusService.findByName("Alquilado")
            .orElseThrow(() -> new RuntimeException("Estado 'Alquilado' no encontrado"));
        
        LocalDateTime rentalDate = LocalDateTime.now();
        LocalDateTime dueDate = rentalDate.plusDays(5);
        BigDecimal dailyRate = BigDecimal.valueOf(getDefaultDailyRate());
        BigDecimal totalCost = dailyRate.multiply(BigDecimal.valueOf(5));
        
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setBookCopy(bookCopy);
        rental.setRentalDate(rentalDate);
        rental.setDueDate(dueDate);
        rental.setRentalDays(5);
        rental.setDailyRate(dailyRate);
        rental.setTotalCost(totalCost);
        rental.setRentalStatus(enProcesoStatus);
        rental.setNotes("Alquiler creado por usuario");
        
        bookCopy.setBookCopyStatus(alquiladoStatus);
        bookCopyRepository.save(bookCopy);
        
        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental createRentalEmergency(UUID userId, UUID bookCopyId) {
        System.out.println("=== MÉTODO DE EMERGENCIA ===");

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

            RentalStatus status = rentalStatusRepository.findByRentalStatusName("En Proceso")
                    .orElseThrow(() -> new RuntimeException("Estado 'En Proceso' no encontrado"));

            BookCopyStatus alquiladoStatus = bookCopyStatusService.findByName("Alquilado")
                    .orElseThrow(() -> new RuntimeException("Estado 'Alquilado' no encontrado"));

            bookCopy.setBookCopyStatus(alquiladoStatus);
            bookCopyRepository.save(bookCopy);
            bookCopyRepository.flush();
            System.out.println("✓ Ejemplar actualizado a Alquilado");

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

    public long countByUser(UUID userId) {
        return rentalRepository.countByUser_UserId(userId);
    }

    public long countActiveByUser(UUID userId) {
        return rentalRepository.countActiveByUser(userId);
    }

    public long countOverdueByUser(UUID userId) {
        return rentalRepository.countOverdueByUser(userId);
    }

    public List<Rental> findLastByUser(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rentalDate"));
        return rentalRepository.findByUser_UserId(userId, pageable).getContent();
    }

    /**
     * Obtiene alquileres con paginación y filtros
     */
    public PagedResult<Rental> getRegisteredRentals(
            int page, 
            int pageSize, 
            String search,
            UUID userId, 
            UUID rentalStatusId) {
        
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Pageable pageable = PageRequest.of(page - 1, pageSize, 
            Sort.by(Sort.Direction.DESC, "rentalDate"));
        
        Page<Rental> springPage = rentalRepository.findAllWithFilters(
            search, userId, rentalStatusId, pageable);
        
        return new PagedResult<>(
            springPage.getContent(),
            page,
            pageSize,
            (int) springPage.getTotalElements()
        );
    }
    
    /**
     * Obtiene todos los estados de alquiler
     */
    public List<RentalStatus> getAllRentalStatuses() {
        return rentalStatusRepository.findAll(Sort.by("rentalStatusName"));
    }
    
    /**
     * Obtiene la tarifa diaria por defecto desde la configuración
     */
    public Double getDefaultDailyRate() {
        try {
            return configurationService.getDecimalValue("RentalDailyRate", Double.valueOf(10.0));
        } catch (Exception e) {
            return Double.valueOf(10.0);
        }
    }
    
    /**
     * Obtiene la penalidad diaria por demora desde la configuración
     */
    public Double getDailyPenalty() {
        try {
            return configurationService.getDecimalValue("ReturnDelayDailyPenalty", Double.valueOf(12.5));
        } catch (Exception e) {
            return Double.valueOf(12.5);
        }
    }
    
    /**
     * Crea un alquiler para el administrador
     * 
     * @param userId ID del usuario
     * @param bookId ID del libro
     * @param rentalDays Número de días de alquiler
     * @param notes Notas opcionales
     * @return Rental creado
     */
    @Transactional
    public Rental createRentalForAdmin(UUID userId, UUID bookId, int rentalDays, String notes) {
        
        // Buscar usuario
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        List<BookCopy> availableCopies = bookCopyRepository.findAvailableByBook(bookId);
        if (availableCopies.isEmpty()) {
            throw new IllegalStateException("No hay ejemplares disponibles para este libro");
        }
        
        BookCopy bookCopy = availableCopies.get(0);
        
        if (!"Disponible".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName())) {
            throw new IllegalStateException("El ejemplar no está disponible");
        }
        
        RentalStatus enProcesoStatus = rentalStatusRepository.findByRentalStatusName("En Proceso")
            .orElseThrow(() -> new RuntimeException("Estado 'En Proceso' no encontrado"));
        
        BookCopyStatus alquiladoStatus = bookCopyStatusService.findByName("Alquilado")
            .orElseThrow(() -> new RuntimeException("Estado 'Alquilado' no encontrado"));
        
        LocalDateTime rentalDate = LocalDateTime.now();
        LocalDateTime dueDate = rentalDate.plusDays(rentalDays);
        BigDecimal dailyRate = BigDecimal.valueOf(getDefaultDailyRate());
        BigDecimal totalCost = dailyRate.multiply(BigDecimal.valueOf(rentalDays));
        
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setBookCopy(bookCopy);
        rental.setRentalDate(rentalDate);
        rental.setDueDate(dueDate);
        rental.setRentalDays(rentalDays);
        rental.setDailyRate(dailyRate);
        rental.setTotalCost(totalCost);
        rental.setRentalStatus(enProcesoStatus);
        rental.setNotes(notes);
        
        bookCopy.setBookCopyStatus(alquiladoStatus);
        bookCopyRepository.save(bookCopy);
        
        return rentalRepository.save(rental);
    }
    
    /**
     * Marca un alquiler como devuelto
     * 
     * @param rentalId ID del alquiler
     */
    @Transactional
    public void markAsReturned(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Alquiler no encontrado"));
        
        if (!"En Proceso".equals(rental.getRentalStatus().getRentalStatusName())) {
            throw new IllegalArgumentException("Solo se pueden marcar como devueltos los alquileres en proceso");
        }
        
        RentalStatus devueltoStatus = rentalStatusRepository.findByRentalStatusName("Devuelto")
            .orElseThrow(() -> new RuntimeException("Estado 'Devuelto' no encontrado"));
        
        BookCopyStatus disponibleStatus = bookCopyStatusService.findByName("Disponible")
            .orElseThrow(() -> new RuntimeException("Estado 'Disponible' no encontrado"));
        
        rental.setRentalStatus(devueltoStatus);
        rental.setReturnDate(LocalDateTime.now());
        rentalRepository.save(rental);
        
        BookCopy bookCopy = rental.getBookCopy();
        bookCopy.setBookCopyStatus(disponibleStatus);
        bookCopyRepository.save(bookCopy);
    }
    
    /**
     * Cancela un alquiler
     * 
     * @param rentalId ID del alquiler
     */
    @Transactional
    public void cancelRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Alquiler no encontrado"));
        
        if (!"En Proceso".equals(rental.getRentalStatus().getRentalStatusName())) {
            throw new IllegalArgumentException("Solo se pueden cancelar los alquileres en proceso");
        }
        
        RentalStatus canceladoStatus = rentalStatusRepository.findByRentalStatusName("Cancelado")
            .orElseThrow(() -> new RuntimeException("Estado 'Cancelado' no encontrado"));
        
        BookCopyStatus disponibleStatus = bookCopyStatusService.findByName("Disponible")
            .orElseThrow(() -> new RuntimeException("Estado 'Disponible' no encontrado"));
        
        rental.setRentalStatus(canceladoStatus);
        rentalRepository.save(rental);
        
        BookCopy bookCopy = rental.getBookCopy();
        bookCopy.setBookCopyStatus(disponibleStatus);
        bookCopyRepository.save(bookCopy);
    }
}
