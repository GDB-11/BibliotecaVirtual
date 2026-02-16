package com.biblioteca.app.service;

import java.time.LocalDateTime;
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

import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.helper.PageMapper;
import com.biblioteca.app.repository.BookCopyRepository;
import com.biblioteca.app.repository.BookRepository;

/**
 * Servicio para la gestión de ejemplares de libros
 */
@Service
@Transactional(readOnly = true)
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyStatusService bookCopyStatusService;

    /**
     * Obtiene un ejemplar por ID (String)
     */
    public BookCopy findById(String bookCopyId) {
        try {
            UUID uuid = UUID.fromString(bookCopyId);
            return bookCopyRepository.findById(uuid).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtiene un ejemplar por ID (UUID)
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
     * Obtiene ejemplares por estado (String)
     */
    public List<BookCopy> findByStatus(String statusId) {
        try {
            UUID uuid = UUID.fromString(statusId);
            return bookCopyRepository.findByBookCopyStatus_BookCopyStatusId(uuid);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    /**
     * Obtiene ejemplares por estado (UUID)
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
     * Obtiene ejemplares registrados con filtros y paginación
     */
    public PagedResult<BookCopy> getRegisteredBookCopies(int page, int pageSize, String search, String bookId, String statusId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<BookCopy> bookCopyPage;

        UUID bookUuid = null;
        UUID statusUuid = null;

        try {
            if (bookId != null && !bookId.trim().isEmpty()) {
                bookUuid = UUID.fromString(bookId);
            }
            if (statusId != null && !statusId.trim().isEmpty()) {
                statusUuid = UUID.fromString(statusId);
            }
        } catch (IllegalArgumentException e) {
            // UUIDs inválidos, se ignorarán
        }

        // Aplicar filtros según los parámetros
        if ((search == null || search.trim().isEmpty()) && bookUuid == null && statusUuid == null) {
            // Sin filtros
            bookCopyPage = bookCopyRepository.findAll(pageable);
        } else if (bookUuid != null && statusUuid != null) {
            // Filtro por libro y estado
            bookCopyPage = bookCopyRepository.findByBook_BookIdAndBookCopyStatus_BookCopyStatusId(bookUuid, statusUuid, pageable);
        } else if (bookUuid != null) {
            // Solo filtro por libro
            bookCopyPage = bookCopyRepository.findByBook_BookId(bookUuid, pageable);
        } else if (statusUuid != null && (search == null || search.trim().isEmpty())) {
            // Solo filtro por estado
            bookCopyPage = bookCopyRepository.findByBookCopyStatus_BookCopyStatusId(statusUuid, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            // Búsqueda por título o ISBN
            String searchTerm = "%" + search.trim() + "%";
            if (statusUuid != null) {
                bookCopyPage = bookCopyRepository.findBySearchAndStatus(searchTerm, statusUuid, pageable);
            } else {
                bookCopyPage = bookCopyRepository.findBySearch(searchTerm, pageable);
            }
        } else {
            bookCopyPage = bookCopyRepository.findAll(pageable);
        }

        return PageMapper.toPagedResult(bookCopyPage);
    }

    /**
     * Guarda o actualiza un ejemplar
     */
    @Transactional
    public BookCopy save(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    /**
     * Actualiza un ejemplar existente
     */
    @Transactional
    public void update(BookCopy bookCopy) {
        bookCopy.setUpdatedAt(LocalDateTime.now());
        bookCopyRepository.save(bookCopy);
    }

    /**
     * Crea múltiples ejemplares en batch
     */
    @Transactional
    public void saveBatch(String bookId, int quantity, String notes) {
        UUID bookUuid = UUID.fromString(bookId);
        Optional<Book> bookOpt = bookRepository.findById(bookUuid);

        if (bookOpt.isEmpty()) {
            throw new RuntimeException("Libro no encontrado");
        }

        UUID availableStatusId = bookCopyStatusService.getAvailableStatusId();
        Optional<BookCopyStatus> availableStatusOpt = bookCopyStatusService.findById(availableStatusId);

        for (int i = 0; i < quantity; i++) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook(bookOpt.get());
            bookCopy.setBookCopyStatus(availableStatusOpt.get());
            bookCopy.setNotes(notes != null && !notes.trim().isEmpty() ? notes : null);
            bookCopy.setCreatedAt(LocalDateTime.now());
            bookCopy.setUpdatedAt(LocalDateTime.now());
            bookCopyRepository.save(bookCopy);
        }
    }

    /**
     * Actualiza el estado de múltiples ejemplares
     */
    @Transactional
    public void updateStatusBatch(List<String> bookCopyIds, String newStatusId) {
        BookCopyStatus newStatus = bookCopyStatusService.findById(UUID.fromString(newStatusId))
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
                
        LocalDateTime now = LocalDateTime.now();

        for (String id : bookCopyIds) {
            try {
                UUID bookCopyUuid = UUID.fromString(id);
                Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(bookCopyUuid);

                if (bookCopyOpt.isPresent()) {
                    BookCopy bookCopy = bookCopyOpt.get();
                    
                    // No actualizar si está alquilado
                    boolean isRented = bookCopy.getBookCopyStatus() != null &&
                            "Alquilado".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName());
                    
                    if (!isRented) {
                        bookCopy.setBookCopyStatus(newStatus);
                        bookCopy.setUpdatedAt(now);
                        bookCopyRepository.save(bookCopy);
                    }
                }
            } catch (IllegalArgumentException e) {
                // UUID inválido, continuar con el siguiente
                continue;
            }
        }
    }

    /**
     * Elimina un ejemplar por ID (String)
     */
    @Transactional
    public void delete(String bookCopyId) {
        try {
            UUID uuid = UUID.fromString(bookCopyId);
            bookCopyRepository.deleteById(uuid);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("ID de ejemplar inválido");
        }
    }

    /**
     * Elimina un ejemplar por ID (UUID)
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

    /**
     * Obtiene el ID del estado "Disponible"
     */
    public String getAvailableStatusId() {
        return bookCopyStatusService.getAvailableStatusId().toString();
    }

    /**
     * Obtiene el ID del estado "Alquilado"
     */
    public String getRentedStatusId() {
        return bookCopyStatusService.getRentedStatusId().toString();
    }

    /**
     * Obtiene el ID del estado "Mantenimiento"
     */
    public String getMaintenanceStatusId() {
        return bookCopyStatusService.getMaintenanceStatusId().toString();
    }

    /**
     * Obtiene el ID del estado "Descontinuado"
     */
    public String getDiscontinuedStatusId() {
        return bookCopyStatusService.getDiscontinuedStatusId().toString();
    }
    // ===== USUARIO =====

    /**
     * Busca ejemplares disponibles de un libro
     */
    public List<BookCopy> findAvailableByBookId(UUID bookId) {
        return bookCopyRepository.findAvailableCopiesByBookId(bookId);
    }

    @Transactional
    public void flush() {
        bookCopyRepository.flush();
    }
}