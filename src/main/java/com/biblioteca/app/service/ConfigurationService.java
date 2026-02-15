package com.biblioteca.app.service;

import com.biblioteca.app.entity.Configuration;
import com.biblioteca.app.entity.enums.ConfigType;
import com.biblioteca.app.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar las configuraciones del sistema.
 */
@Service
public class ConfigurationService {
    
    private final ConfigurationRepository configurationRepository;
    
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }
    
    /**
     * Obtiene una configuración por su clave.
     *
     * @param configKey La clave de configuración
     * @return La configuración encontrada
     * @throws IllegalArgumentException si no se encuentra la configuración
     */
    public Configuration getByKey(String configKey) {
        return configurationRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada: " + configKey));
    }
    
    /**
     * Obtiene una configuración por su ID.
     *
     * @param configurationId El ID de la configuración
     * @return La configuración encontrada
     * @throws IllegalArgumentException si no se encuentra la configuración
     */
    public Configuration getById(UUID configurationId) {
        return configurationRepository.findById(configurationId)
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada con ID: " + configurationId));
    }
    
    /**
     * Obtiene todas las configuraciones ordenadas por DisplayName.
     *
     * @return Lista de todas las configuraciones
     */
    public List<Configuration> getAllConfigurations() {
        return configurationRepository.findAllByOrderByDisplayNameAsc();
    }
    
    /**
     * Obtiene todas las configuraciones de un tipo específico.
     *
     * @param configType El tipo de configuración
     * @return Lista de configuraciones del tipo especificado
     */
    public List<Configuration> getConfigurationsByType(ConfigType configType) {
        return configurationRepository.findByConfigTypeOrderByDisplayNameAsc(configType);
    }
    
    /**
     * Actualiza una configuración existente.
     *
     * @param configuration La configuración a actualizar
     * @throws IllegalArgumentException si la configuración no existe o el valor es inválido
     */
    @Transactional
    public void updateConfiguration(Configuration configuration) {
        Configuration existing = configurationRepository.findById(configuration.getConfigurationId())
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada"));
        
        validateConfigValue(configuration);
        
        existing.setConfigValue(configuration.getConfigValue());
        existing.setConfigType(configuration.getConfigType());
        existing.setDisplayName(configuration.getDisplayName());
        existing.setDescription(configuration.getDescription());
        
        configurationRepository.save(existing);
    }
    
    /**
     * Actualiza solo el valor de una configuración.
     *
     * @param configKey La clave de configuración
     * @param newValue El nuevo valor
     * @throws IllegalArgumentException si la configuración no existe o el valor es inválido
     */
    @Transactional
    public void updateConfigValue(String configKey, String newValue) {
        Configuration config = getByKey(configKey);
        config.setConfigValue(newValue);
        validateConfigValue(config);
        configurationRepository.save(config);
    }
    
    /**
     * Obtiene un valor entero de configuración con valor por defecto.
     *
     * @param configKey La clave de configuración
     * @param defaultValue El valor por defecto si no se encuentra o hay error
     * @return El valor entero de la configuración
     */
    public int getIntValue(String configKey, int defaultValue) {
        try {
            return configurationRepository.findByConfigKey(configKey)
                    .map(config -> Integer.parseInt(config.getConfigValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtiene un valor de texto de configuración con valor por defecto.
     *
     * @param configKey La clave de configuración
     * @param defaultValue El valor por defecto si no se encuentra
     * @return El valor de texto de la configuración
     */
    public String getStringValue(String configKey, String defaultValue) {
        return configurationRepository.findByConfigKey(configKey)
                .map(Configuration::getConfigValue)
                .orElse(defaultValue);
    }
    
    /**
     * Obtiene un valor booleano de configuración con valor por defecto.
     *
     * @param configKey La clave de configuración
     * @param defaultValue El valor por defecto si no se encuentra
     * @return El valor booleano de la configuración
     */
    public boolean getBooleanValue(String configKey, boolean defaultValue) {
        return configurationRepository.findByConfigKey(configKey)
                .map(config -> {
                    String value = config.getConfigValue().toLowerCase();
                    return value.equals("true") || value.equals("1") || value.equals("yes");
                })
                .orElse(defaultValue);
    }
    
    /**
     * Obtiene un valor decimal de configuración con valor por defecto.
     *
     * @param configKey La clave de configuración
     * @param defaultValue El valor por defecto si no se encuentra o hay error
     * @return El valor decimal de la configuración
     */
    public double getDecimalValue(String configKey, double defaultValue) {
        try {
            return configurationRepository.findByConfigKey(configKey)
                    .map(config -> Double.parseDouble(config.getConfigValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Verifica si existe una configuración con la clave dada.
     *
     * @param configKey La clave de configuración
     * @return true si existe, false si no
     */
    public boolean existsByKey(String configKey) {
        return configurationRepository.existsByConfigKey(configKey);
    }
    
    /**
     * Valida que el valor de configuración sea del tipo correcto.
     *
     * @param config La configuración a validar
     * @throws IllegalArgumentException si el valor no es válido para el tipo
     */
    private void validateConfigValue(Configuration config) {
        ConfigType type = config.getConfigType();
        String value = config.getConfigValue();
        
        try {
            switch (type) {
                case INT:
                    Integer.parseInt(value);
                    break;
                case DECIMAL:
                    Double.parseDouble(value);
                    break;
                case BOOLEAN:
                    String normalizedValue = value.toLowerCase();
                    if (!normalizedValue.equals("true") && !normalizedValue.equals("false") &&
                        !normalizedValue.equals("1") && !normalizedValue.equals("0") &&
                        !normalizedValue.equals("yes") && !normalizedValue.equals("no")) {
                        throw new IllegalArgumentException("Valor booleano inválido");
                    }
                    break;
                case STRING:
                    // No se necesita validación especial para strings
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de configuración desconocido: " + type);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El valor no es válido para el tipo " + type);
        }
    }
}