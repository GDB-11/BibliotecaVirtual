package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "Country")
public class Country {

    @Id
    @UuidGenerator
    @Column(name = "CountryId", columnDefinition = "BINARY(16)", nullable = false)
    private UUID countryId;

    @Column(name = "CountryName", length = 100, nullable = false, unique = true)
    private String countryName;

    @Column(name = "CountryCode", length = 3)
    private String countryCode;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public Country() {
    }

    public Country(UUID countryId, String countryName, String countryCode) {
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public Country(UUID countryId, String countryName, String countryCode,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
        return countryName;
    }
}