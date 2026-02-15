package com.biblioteca.app.repository;

import com.biblioteca.app.entity.Configuration;
import com.biblioteca.app.entity.enums.ConfigType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Configuration.
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {
    
    /**
     * Busca una configuración por su clave única.
     *
     * @param configKey La clave de configuración
     * @return Optional conteniendo la configuración si existe
     */
    Optional<Configuration> findByConfigKey(String configKey);
    
    /**
     * Verifica si existe una configuración con la clave dada.
     *
     * @param configKey La clave de configuración
     * @return true si existe, false si no
     */
    boolean existsByConfigKey(String configKey);
    
    /**
     * Obtiene todas las configuraciones ordenadas por DisplayName.
     *
     * @return Lista de configuraciones ordenadas
     */
    List<Configuration> findAllByOrderByDisplayNameAsc();
    
    /**
     * Obtiene todas las configuraciones de un tipo específico.
     *
     * @param configType El tipo de configuración
     * @return Lista de configuraciones del tipo especificado
     */
    List<Configuration> findByConfigType(ConfigType configType);
    
    /**
     * Obtiene todas las configuraciones de un tipo específico ordenadas por DisplayName.
     *
     * @param configType El tipo de configuración
     * @return Lista de configuraciones del tipo especificado ordenadas
     */
    List<Configuration> findByConfigTypeOrderByDisplayNameAsc(ConfigType configType);
}