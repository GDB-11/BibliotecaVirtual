package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "BookCopy")
public class BookCopy {

	@Id
	@UuidGenerator
	@Column(name = "BookCopyId", columnDefinition = "BINARY(16)", nullable = false)
	private UUID bookCopyId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BookId", nullable = false, foreignKey = @ForeignKey(name = "fk_bookcopy_book"))
	private Book book;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BookCopyStatusId", nullable = false, foreignKey = @ForeignKey(name = "fk_bookcopy_status"))
	private BookCopyStatus bookCopyStatus;

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
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
	
	// Constructores
	public BookCopy() {
	}

	public BookCopy(UUID bookCopyId, Book book, BookCopyStatus bookCopyStatus) {
		this.bookCopyId = bookCopyId;
		this.book = book;
		this.bookCopyStatus = bookCopyStatus;
	}

	// Getters y Setters
	public UUID getBookCopyId() {
		return bookCopyId;
	}

	public void setBookCopyId(UUID bookCopyId) {
		this.bookCopyId = bookCopyId;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public BookCopyStatus getBookCopyStatus() {
		return bookCopyStatus;
	}

	public void setBookCopyStatus(BookCopyStatus bookCopyStatus) {
		this.bookCopyStatus = bookCopyStatus;
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
		return book != null ? book.getTitle() + " - Copia #" + bookCopyId : "Copia #" + bookCopyId;
	}
}