package com.biblioteca.app.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "Rental")
public class Rental {

	@Id
	@UuidGenerator
	@Column(name = "RentalId", columnDefinition = "BINARY(16)", nullable = false)
	private UUID rentalId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "UserId", nullable = false, foreignKey = @ForeignKey(name = "fk_rental_user"))
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BookCopyId", nullable = false, foreignKey = @ForeignKey(name = "fk_rental_bookcopy"))
	private BookCopy bookCopy;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RentalStatusId", nullable = false, foreignKey = @ForeignKey(name = "fk_rental_status"))
	private RentalStatus rentalStatus;

	@Column(name = "RentalDate", nullable = false)
	private LocalDateTime rentalDate;

	@Column(name = "DueDate", nullable = false)
	private LocalDateTime dueDate;

	@Column(name = "ReturnDate")
	private LocalDateTime returnDate;

	@Column(name = "RentalDays", nullable = false)
	private Integer rentalDays;

	@Column(name = "DailyRate", precision = 10, scale = 2, nullable = false)
	private BigDecimal dailyRate = BigDecimal.ZERO;

	@Column(name = "TotalCost", precision = 10, scale = 2, nullable = false)
	private BigDecimal totalCost = BigDecimal.ZERO;

	@Column(name = "Notes", columnDefinition = "TEXT")
	private String notes;

	@Column(name = "CreatedAt", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (rentalDate == null) {
			rentalDate = LocalDateTime.now();
		}
		if (dailyRate == null) {
			dailyRate = BigDecimal.ZERO;
		}
		if (totalCost == null) {
			totalCost = BigDecimal.ZERO;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Constructores
	public Rental() {
	}

	public Rental(UUID rentalId, User user, BookCopy bookCopy, RentalStatus rentalStatus,
			LocalDateTime rentalDate, LocalDateTime dueDate, Integer rentalDays,
			BigDecimal dailyRate, BigDecimal totalCost) {
		this.rentalId = rentalId;
		this.user = user;
		this.bookCopy = bookCopy;
		this.rentalStatus = rentalStatus;
		this.rentalDate = rentalDate;
		this.dueDate = dueDate;
		this.rentalDays = rentalDays;
		this.dailyRate = dailyRate;
		this.totalCost = totalCost;
	}

	// Getters y Setters
	public UUID getRentalId() {
		return rentalId;
	}

	public void setRentalId(UUID rentalId) {
		this.rentalId = rentalId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BookCopy getBookCopy() {
		return bookCopy;
	}

	public void setBookCopy(BookCopy bookCopy) {
		this.bookCopy = bookCopy;
	}

	public RentalStatus getRentalStatus() {
		return rentalStatus;
	}

	public void setRentalStatus(RentalStatus rentalStatus) {
		this.rentalStatus = rentalStatus;
	}

	public LocalDateTime getRentalDate() {
		return rentalDate;
	}

	public void setRentalDate(LocalDateTime rentalDate) {
		this.rentalDate = rentalDate;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public LocalDateTime getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDateTime returnDate) {
		this.returnDate = returnDate;
	}

	public Integer getRentalDays() {
		return rentalDays;
	}

	public void setRentalDays(Integer rentalDays) {
		this.rentalDays = rentalDays;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(BigDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
		return "Rental #" + rentalId;
	}
}