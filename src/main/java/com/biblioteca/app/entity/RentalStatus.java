package com.biblioteca.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RentalStatus")
public class RentalStatus {

    @Id
    @Column(name = "RentalStatusId")
    private Long rentalStatusId;

    @Column(name = "RentalStatusName")
    private String rentalStatusName;

	public Long getRentalStatusId() {
		return rentalStatusId;
	}

	public String getRentalStatusName() {
		return rentalStatusName;
	}

	public void setRentalStatusId(Long rentalStatusId) {
		this.rentalStatusId = rentalStatusId;
	}

	public void setRentalStatusName(String rentalStatusName) {
		this.rentalStatusName = rentalStatusName;
	}
    
    
}
