package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "BookStatus")
public class BookStatus {

    @Id
    @UuidGenerator
    @Column(name = "BookStatusId", columnDefinition = "BINARY(16)", nullable = false)
    private UUID bookStatusId;

    @Column(name = "BookStatusName", length = 50, nullable = false, unique = true)
    private String bookStatusName;

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
    public BookStatus() {
    }

    public BookStatus(UUID bookStatusId, String bookStatusName) {
        this.bookStatusId = bookStatusId;
        this.bookStatusName = bookStatusName;
    }

    public BookStatus(UUID bookStatusId, String bookStatusName,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookStatusId = bookStatusId;
        this.bookStatusName = bookStatusName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public UUID getBookStatusId() {
        return bookStatusId;
    }

    public void setBookStatusId(UUID bookStatusId) {
        this.bookStatusId = bookStatusId;
    }

    public String getBookStatusName() {
        return bookStatusName;
    }

    public void setBookStatusName(String bookStatusName) {
        this.bookStatusName = bookStatusName;
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
        return bookStatusName;
    }
}