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
@Table(name = "BookCopyStatus")
public class BookCopyStatus {

    @Id
    @UuidGenerator
    @Column(name = "BookCopyStatusId", columnDefinition = "BINARY(16)", nullable = false)
    private UUID bookCopyStatusId;

    @Column(name = "BookCopyStatusName", length = 50, nullable = false, unique = true)
    private String bookCopyStatusName;

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
    public BookCopyStatus() {
    }

    public BookCopyStatus(UUID bookCopyStatusId, String bookCopyStatusName) {
        this.bookCopyStatusId = bookCopyStatusId;
        this.bookCopyStatusName = bookCopyStatusName;
    }

    public BookCopyStatus(UUID bookCopyStatusId, String bookCopyStatusName,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookCopyStatusId = bookCopyStatusId;
        this.bookCopyStatusName = bookCopyStatusName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public UUID getBookCopyStatusId() {
        return bookCopyStatusId;
    }

    public void setBookCopyStatusId(UUID bookCopyStatusId) {
        this.bookCopyStatusId = bookCopyStatusId;
    }

    public String getBookCopyStatusName() {
        return bookCopyStatusName;
    }

    public void setBookCopyStatusName(String bookCopyStatusName) {
        this.bookCopyStatusName = bookCopyStatusName;
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
        return bookCopyStatusName;
    }
}