package com.biblioteca.app.repository;

import com.biblioteca.app.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad Country.
 * Proporciona acceso a datos de países.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {
    
    /**
     * Busca un país por su nombre.
     * 
     * @param countryName Nombre del país
     * @return Optional con el país si existe
     */
    Optional<Country> findByCountryName(String countryName);
    
    /**
     * Busca un país por su código.
     * 
     * @param countryCode Código del país (ISO 3166-1 alpha-3)
     * @return Optional con el país si existe
     */
    Optional<Country> findByCountryCode(String countryCode);
    
    /**
     * Verifica si existe un país con el nombre dado.
     * 
     * @param countryName Nombre del país
     * @return true si existe, false si no
     */
    boolean existsByCountryName(String countryName);
    
    /**
     * Verifica si existe un país con el código dado.
     * 
     * @param countryCode Código del país
     * @return true si existe, false si no
     */
    boolean existsByCountryCode(String countryCode);
}