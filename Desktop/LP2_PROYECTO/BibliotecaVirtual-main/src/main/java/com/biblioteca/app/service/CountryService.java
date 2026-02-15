package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biblioteca.app.entity.Country;
import com.biblioteca.app.repository.CountryRepository;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Obtiene todos los países
     */
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    /**
     * Busca un país por ID
     */
    public Optional<Country> findById(UUID id) {
        return countryRepository.findById(id);
    }

    /**
     * Busca un país por nombre
     */
    public Optional<Country> findByCountryName(String name) {
        return countryRepository.findByCountryName(name);
    }

    /**
     * Guarda un país
     */
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    /**
     * Elimina un país por ID
     */
    public void deleteById(UUID id) {
        countryRepository.deleteById(id);
    }

    /**
     * Cuenta el total de países
     */
    public long count() {
        return countryRepository.count();
    }
}
