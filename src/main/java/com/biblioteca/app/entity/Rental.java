package com.biblioteca.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Rental")
public class Rental {

    @Id
    @Column(name = "RentalId")
    private Long rentalId;

    @Column(name = "UserId")
    private Long userId;

    @Column(name = "BookCopyId")
    private Long bookCopyId;

    @Column(name = "RentalDate")
    private LocalDateTime rentalDate;

    @Column(name = "DueDate")
    private LocalDateTime dueDate;

    @Column(name = "ReturnDate")
    private LocalDateTime returnDate;

    @Column(name = "TotalCost")
    private Double totalCost;

    @Column(name = "RentalStatusId")
    private Long rentalStatusId;

	public Long getRentalId() {
		return rentalId;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getBookCopyId() {
		return bookCopyId;
	}

	public LocalDateTime getRentalDate() {
		return rentalDate;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public LocalDateTime getReturnDate() {
		return returnDate;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public Long getRentalStatusId() {
		return rentalStatusId;
	}

	public void setRentalId(Long rentalId) {
		this.rentalId = rentalId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setBookCopyId(Long bookCopyId) {
		this.bookCopyId = bookCopyId;
	}

	public void setRentalDate(LocalDateTime rentalDate) {
		this.rentalDate = rentalDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public void setReturnDate(LocalDateTime returnDate) {
		this.returnDate = returnDate;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public void setRentalStatusId(Long rentalStatusId) {
		this.rentalStatusId = rentalStatusId;
	}
    
    
    
}
