package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "Book")
public class Book {

	@Id
	@UuidGenerator
	@Column(name = "BookId", columnDefinition = "BINARY(16)", nullable = false)
	private UUID bookId;

	@Column(name = "ISBN", length = 20, nullable = false, unique = true)
	private String isbn;

	@Column(name = "Title", length = 500, nullable = false)
	private String title;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AuthorId", nullable = false, foreignKey = @ForeignKey(name = "fk_book_author"))
	private Author author;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CategoryId", nullable = false, foreignKey = @ForeignKey(name = "fk_book_category"))
	private Category category;

	@Column(name = "PublicationYear")
	private Integer publicationYear;
	     

	@Column(name = "Publisher", length = 255)
	private String publisher;

	@Column(name = "Pages")
	private Integer pages;

	@Column(name = "Language", length = 50)
	private String language = "Español";

	@Column(name = "Description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "CoverImageUrl", length = 500)
	private String coverImageUrl;

	// Relación ManyToOne con BookStatus
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BookStatusId", nullable = false, foreignKey = @ForeignKey(name = "fk_book_bookstatus"))
	private BookStatus bookStatus;

	@Column(name = "CreatedAt", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (language == null) {
			language = "Español";
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Constructores
	public Book() {
	}

	public Book(UUID bookId, String isbn, String title, Author author,
			Category category, BookStatus bookStatus) {
		this.bookId = bookId;
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.category = category;
		this.bookStatus = bookStatus;
	}

	// Getters y Setters
	public UUID getBookId() {
		return bookId;
	}

	public void setBookId(UUID bookId) {
		this.bookId = bookId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public BookStatus getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(BookStatus bookStatus) {
		this.bookStatus = bookStatus;
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

	public String getPublicationYearString() {
		if (publicationYear == null) {
			return "-";
		}

		if (publicationYear <= 0) {
			int bcYear = -publicationYear + 1;
			return bcYear + " a.C.";
		} else {
			return String.valueOf(publicationYear);
		}
	}

	
	
	

	@Override
	public String toString() {
		return title;
	}
	
	
	
}