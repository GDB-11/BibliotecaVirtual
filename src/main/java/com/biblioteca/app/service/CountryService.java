package com.biblioteca.app.service;

import com.biblioteca.app.entity.Country;
import com.biblioteca.app.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la gestión de países.
 * Proporciona operaciones CRUD y consultas relacionadas con países.
 */
@Service
@Transactional(readOnly = true)
public class CountryService {
    
    @Autowired
    private CountryRepository countryRepository;
    
    /**
     * Obtiene todos los países ordenados por nombre.
     * 
     * @return Lista de todos los países
     */
    public List<Country> findAll() {
        return countryRepository.findAll(Sort.by("countryName").ascending());
    }
    
    /**
     * Obtiene un país por su ID.
     * 
     * @param countryId UUID del país
     * @return Optional con el país si existe
     */
    public Optional<Country> findById(UUID countryId) {
        return countryRepository.findById(countryId);
    }
    
    /**
     * Obtiene un país por su nombre.
     * 
     * @param countryName Nombre del país
     * @return Optional con el país si existe
     */
    public Optional<Country> findByName(String countryName) {
        return countryRepository.findByCountryName(countryName);
    }
    
    /**
     * Obtiene un país por su código.
     * 
     * @param countryCode Código del país (ISO 3166-1 alpha-3)
     * @return Optional con el país si existe
     */
    public Optional<Country> findByCode(String countryCode) {
        return countryRepository.findByCountryCode(countryCode);
    }
    
    /**
     * Verifica si existe un país con el nombre dado.
     * 
     * @param countryName Nombre del país
     * @return true si existe, false si no
     */
    public boolean existsByName(String countryName) {
        return countryRepository.existsByCountryName(countryName);
    }
    
    /**
     * Verifica si existe un país con el código dado.
     * 
     * @param countryCode Código del país
     * @return true si existe, false si no
     */
    public boolean existsByCode(String countryCode) {
        return countryRepository.existsByCountryCode(countryCode);
    }
    
    /**
     * Guarda o actualiza un país.
     * 
     * @param country País a guardar
     * @return País guardado
     */
    @Transactional
    public Country save(Country country) {
        return countryRepository.save(country);
    }
    
    /**
     * Elimina un país por su ID.
     * 
     * @param countryId UUID del país a eliminar
     */
    @Transactional
    public void delete(UUID countryId) {
        countryRepository.deleteById(countryId);
    }
    
    /**
     * Cuenta el total de países.
     * 
     * @return Cantidad de países
     */
    public long count() {
        return countryRepository.count();
    }
}