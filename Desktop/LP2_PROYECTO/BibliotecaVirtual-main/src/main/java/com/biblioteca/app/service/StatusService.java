package com.biblioteca.app.service;

import com.biblioteca.app.entity.Status;
import com.biblioteca.app.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la gestión de estados.
 * Proporciona operaciones CRUD y consultas relacionadas con estados.
 */
@Service
@Transactional(readOnly = true)
public class StatusService {
    
    @Autowired
    private StatusRepository statusRepository;
    
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
    
    /**
     * Cuenta el total de estados.
     * 
     * @return Cantidad de estados
     */
    public long count() {
        return statusRepository.count();
    }
}