package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "Category")
public class Category {

	@Id
	@UuidGenerator
	@Column(name = "CategoryId", columnDefinition = "BINARY(16)", nullable = false)
	private UUID categoryId;

	@Column(name = "CategoryName", length = 100, nullable = false, unique = true)
	private String categoryName;

	@Column(name = "Description", columnDefinition = "TEXT")
	private String description;

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
	public Category() {
	}

	public Category(UUID categoryId, String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	public Category(UUID categoryId, String categoryName, String description,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// Getters y Setters
	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
		return categoryName;
	}
}