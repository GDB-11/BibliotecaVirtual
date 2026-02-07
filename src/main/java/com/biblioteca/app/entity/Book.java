package com.biblioteca.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "BookId")
    private Long bookId;

    @Column(name = "Title")
    private String title;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "PublicationYear")
    private Integer publicationYear;

    @Column(name = "AuthorId")
    private Long authorId;

    @Column(name = "CategoryId")
    private Long categoryId;

    @Column(name = "BookStatusId")
    private Long bookStatusId;

	public Long getBookId() {
		return bookId;
	}

	public String getTitle() {
		return title;
	}

	public String getIsbn() {
		return isbn;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public Long getBookStatusId() {
		return bookStatusId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setBookStatusId(Long bookStatusId) {
		this.bookStatusId = bookStatusId;
	}

    // getters y setters
    
}
