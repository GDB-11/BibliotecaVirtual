package com.biblioteca.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "`User`")
public class User {

    @Id
    @Column(name = "UserId")
    private Long userId;

    @Column(name = "Email")
    private String email;

	public Long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    
}
