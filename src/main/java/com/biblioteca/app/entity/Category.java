package com.biblioteca.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Category")
public class Category {

    @Id
    @Column(name = "CategoryId")
    private Long categoryId;

    @Column(name = "CategoryName")
    private String categoryName;

	public Long getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
    
    
}
