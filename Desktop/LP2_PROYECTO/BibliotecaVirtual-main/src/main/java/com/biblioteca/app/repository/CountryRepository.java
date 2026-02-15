package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {

    /**
     * Busca un país por su nombre
     */
    Optional<Country> findByCountryName(String countryName);

    /**
     * Verifica si existe un país con el nombre dado
     */
    boolean existsByCountryName(String countryName);
}