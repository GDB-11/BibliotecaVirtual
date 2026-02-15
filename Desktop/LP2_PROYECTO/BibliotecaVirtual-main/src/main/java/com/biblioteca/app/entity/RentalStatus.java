package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "RentalStatus")
public class RentalStatus {

	@Id
	@UuidGenerator
	@Column(name = "RentalStatusId", columnDefinition = "BINARY(16)", nullable = false)
	private UUID rentalStatusId;

	@Column(name = "RentalStatusName", length = 50, nullable = false, unique = true)
	private String rentalStatusName;

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
	public RentalStatus() {
	}

	public RentalStatus(UUID rentalStatusId, String rentalStatusName) {
		this.rentalStatusId = rentalStatusId;
		this.rentalStatusName = rentalStatusName;
	}

	public RentalStatus(UUID rentalStatusId, String rentalStatusName,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.rentalStatusId = rentalStatusId;
		this.rentalStatusName = rentalStatusName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// Getters y Setters
	public UUID getRentalStatusId() {
		return rentalStatusId;
	}

	public void setRentalStatusId(UUID rentalStatusId) {
		this.rentalStatusId = rentalStatusId;
	}

	public String getRentalStatusName() {
		return rentalStatusName;
	}

	public void setRentalStatusName(String rentalStatusName) {
		this.rentalStatusName = rentalStatusName;
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
		return rentalStatusName;
	}
}