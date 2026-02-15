package com.biblioteca.app.service;

import com.biblioteca.app.entity.BookStatus;
import com.biblioteca.app.entity.Status;
import com.biblioteca.app.repository.BookStatusRepository;
import com.biblioteca.app.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la gestión de estados y estados de libros.
 * Proporciona operaciones CRUD y consultas relacionadas con estados.
 */
@Service
@Transactional(readOnly = true)
public class StatusService {
    
    @Autowired
    private StatusRepository statusRepository;
    
    @Autowired
    private BookStatusRepository bookStatusRepository;
    
    /**
     * Obtiene todos los estados del sistema.
     * 
     * @return Lista de todos los estados
     */
    public List<Status> findAll() {
        return statusRepository.findAll();
    }
    
    /**
     * Obtiene un estado por su ID.
     * 
     * @param statusId UUID del estado
     * @return Optional con el estado si existe
     */
    public Optional<Status> findById(UUID statusId) {
        return statusRepository.findById(statusId);
    }
    
    /**
     * Obtiene un estado por su nombre.
     * 
     * @param statusName Nombre del estado (Active, Inactive, etc.)
     * @return Optional con el estado si existe
     */
    public Optional<Status> findByName(String statusName) {
        return statusRepository.findByStatusName(statusName);
    }
    
    /**
     * Verifica si existe un estado con el nombre dado.
     * 
     * @param statusName Nombre del estado
     * @return true si existe, false si no
     */
    public boolean existsByName(String statusName) {
        return statusRepository.existsByStatusName(statusName);
    }
    
    /**
     * Guarda o actualiza un estado.
     * 
     * @param status Estado a guardar
     * @return Estado guardado
     */
    @Transactional
    public Status save(Status status) {
        return statusRepository.save(status);
    }
    
    /**
     * Elimina un estado por su ID.
     * 
     * @param statusId UUID del estado a eliminar
     */
    @Transactional
    public void delete(UUID statusId) {
        statusRepository.deleteById(statusId);
    }
    
    /**
     * Obtiene el ID del estado Active.
     * 
     * @return UUID del estado Active
     * @throws IllegalStateException si no existe el estado Active
     */
    public UUID getActiveStatusId() {
        return statusRepository.findByStatusName("Active")
            .orElseThrow(() -> new IllegalStateException("Estado Active no encontrado"))
            .getStatusId();
    }
    
    /**
     * Obtiene el ID del estado Inactive.
     * 
     * @return UUID del estado Inactive
     * @throws IllegalStateException si no existe el estado Inactive
     */
    public UUID getInactiveStatusId() {
        return statusRepository.findByStatusName("Inactive")
            .orElseThrow(() -> new IllegalStateException("Estado Inactive no encontrado"))
            .getStatusId();
    }
    
    // ==================== BOOK STATUS METHODS ====================
    
    /**
     * Obtiene todos los estados de libros.
     * 
     * @return Lista de todos los estados de libros
     */
    public List<BookStatus> findAllBookStatuses() {
        return bookStatusRepository.findAll();
    }
    
    /**
     * Obtiene un estado de libro por su ID.
     * 
     * @param bookStatusId UUID del estado de libro
     * @return Optional con el estado de libro si existe
     */
    public Optional<BookStatus> findBookStatusById(UUID bookStatusId) {
        return bookStatusRepository.findById(bookStatusId);
    }
    
    /**
     * Obtiene un estado de libro por su nombre.
     * 
     * @param bookStatusName Nombre del estado de libro
     * @return Optional con el estado de libro si existe
     */
    public Optional<BookStatus> findBookStatusByName(String bookStatusName) {
        return bookStatusRepository.findByBookStatusName(bookStatusName);
    }
    
    /**
     * Verifica si existe un estado de libro con el nombre dado.
     * 
     * @param bookStatusName Nombre del estado de libro
     * @return true si existe, false si no
     */
    public boolean existsBookStatusByName(String bookStatusName) {
        return bookStatusRepository.existsByBookStatusName(bookStatusName);
    }
    
    /**
     * Guarda o actualiza un estado de libro.
     * 
     * @param bookStatus Estado de libro a guardar
     * @return Estado de libro guardado
     */
    @Transactional
    public BookStatus saveBookStatus(BookStatus bookStatus) {
        return bookStatusRepository.save(bookStatus);
    }
    
    /**
     * Elimina un estado de libro por su ID.
     * 
     * @param bookStatusId UUID del estado de libro a eliminar
     */
    @Transactional
    public void deleteBookStatus(UUID bookStatusId) {
        bookStatusRepository.deleteById(bookStatusId);
    }
    
    
    
    
    
    //USUARIO
    /**
     * Busca un estado por nombre
     */
    public Optional<Status> findByStatusName(String statusName) {
        return statusRepository.findByStatusName(statusName);
    }
    
    
    
    
}