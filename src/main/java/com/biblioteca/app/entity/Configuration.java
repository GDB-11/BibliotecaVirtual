package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.biblioteca.app.entity.enums.ConfigType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "Configuration")
public class Configuration {

    @Id
    @UuidGenerator
    @Column(name = "ConfigurationId", columnDefinition = "BINARY(16)", nullable = false)
    private UUID configurationId;

    @Column(name = "ConfigKey", length = 100, nullable = false, unique = true)
    private String configKey;

    @Column(name = "ConfigValue", length = 500, nullable = false)
    private String configValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "ConfigType", nullable = false, columnDefinition = "ENUM('INT','DECIMAL','STRING','BOOLEAN')")
    private ConfigType configType = ConfigType.STRING;

    @Column(name = "DisplayName", length = 100, nullable = false)
    private String displayName;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (configType == null) {
            configType = ConfigType.STRING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public Configuration() {
    }

    public Configuration(UUID configurationId, String configKey, String configValue,
            ConfigType configType, String displayName, String description) {
        this.configurationId = configurationId;
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.displayName = displayName;
        this.description = description;
    }

    public Configuration(UUID configurationId, String configKey, String configValue,
            ConfigType configType, String displayName, String description,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.configurationId = configurationId;
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.displayName = displayName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public UUID getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(UUID configurationId) {
        this.configurationId = configurationId;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return configKey + ": " + configValue;
    }
}